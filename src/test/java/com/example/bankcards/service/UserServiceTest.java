package com.example.bankcards.service;

import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.UserServiceImpl;
import com.example.bankcards.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .username("test")
                .password("password")
                .role(Role.USER)
                .build();

        userResponse = UserResponse.builder().id(1L).username("test").build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );
    }

    @Test
    void getMyUserInfo_shouldReturnUser() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getMyUserInfo();

        assertThat(result).isEqualTo(userResponse);
        verify(userRepository).findByUsername("test");
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getMyUserInfo_shouldThrowUserNotFoundException() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyUserInfo())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь test не найден");
    }

    @Test
    void getAllUsers_shouldReturnUsers() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAllUsers(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(userResponse);
        verify(userRepository).findAll(any(Pageable.class));
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertThat(result).isEqualTo(userResponse);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с id - 1 не найден");
    }

    @Test
    void updateUser_shouldUpdateUser() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("newUserTest");
        dto.setPassword("newPasswordTest");
        dto.setRole(Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode("newPasswordTest")).thenReturn("encodedPasswordTest");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser(dto, 1L);

        assertThat(user.getUsername()).isEqualTo("newUserTest");
        assertThat(user.getPassword()).isEqualTo("encodedPasswordTest");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result).isEqualTo(userResponse);
    }

    @Test
    void updateUser_shouldThrowUserNotFoundException() {
        UserUpdateDto dto = new UserUpdateDto();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(dto, 1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с id - 1 не найден");
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowUserNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с id - 1 не найден");
    }


}
