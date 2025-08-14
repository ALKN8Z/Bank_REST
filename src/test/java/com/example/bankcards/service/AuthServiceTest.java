package com.example.bankcards.service;


import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UsernameAlreadyTakenException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtProvider;
import com.example.bankcards.security.MyUserDetails;
import com.example.bankcards.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("test");
        authRequest.setPassword("password");
    }

    @Test
    void login_shouldLogInSuccessfully() {
        MyUserDetails userDetails = new MyUserDetails(
                User.builder().username("test").password("encodedPassword").role(Role.USER).build()
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtProvider.generateToken("test")).thenReturn("token");

        String token = authService.login(authRequest);

        assertThat(token).isEqualTo("token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider).generateToken("test");
    }

    @Test
    void register_shouldRegisterSuccessfully() {
        when(userRepository.existsUserByUsername("test")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtProvider.generateToken("test")).thenReturn("token");

        String token = authService.register(authRequest);

        assertThat(token).isEqualTo("token");
        verify(userRepository).save(any(User.class));
        verify(jwtProvider).generateToken("test");
    }

    @Test
    void register_shouldThrowUsernameAlreadyTakenException() {
        when(userRepository.existsUserByUsername("test")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(authRequest))
                .isInstanceOf(UsernameAlreadyTakenException.class)
                .hasMessageContaining("Данное имя пользователя уже занято");

        verify(userRepository, never()).save(any(User.class));
        verify(jwtProvider, never()).generateToken(anyString());
    }

}
