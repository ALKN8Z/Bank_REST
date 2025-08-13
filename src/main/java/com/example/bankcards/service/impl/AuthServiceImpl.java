package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.security.MyUserDetails;
import com.example.bankcards.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
        ));

        return jwtProvider.generateToken(((MyUserDetails) authentication.getPrincipal()).getUsername());
    }

    @Override
    @Transactional
    public String register(AuthRequest authRequest) {
        if (userRepository.existsUserByUsername(authRequest.getUsername())) {
            throw new IllegalArgumentException("Данное имя пользователя уже занято");
        }
        else{
            User newUser = User.builder()
                    .username(authRequest.getUsername())
                    .password(bCryptPasswordEncoder.encode(authRequest.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(newUser);

            return jwtProvider.generateToken(newUser.getUsername());
        }
    }


}
