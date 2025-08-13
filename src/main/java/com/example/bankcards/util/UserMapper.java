package com.example.bankcards.util;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final CardMapper cardMapper;

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .cards(user.getCards().stream().map(cardMapper::toCardResponse).collect(Collectors.toSet()))
                .role(user.getRole())
                .build();
    }
}
