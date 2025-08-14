package com.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.UserUpdateDto;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = UserResponse.builder()
                .id(1L)
                .username("test")
                .build();
    }

    @Test
    @WithMockUser
    void getMyUserInfo_shouldReturnUserInfo() throws Exception {
        Mockito.when(userService.getMyUserInfo()).thenReturn(sampleUser);

        mockMvc.perform(get("/api/users/my-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleUser.getId()))
                .andExpect(jsonPath("$.username").value(sampleUser.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnPagedUsers() throws Exception {
        Page<UserResponse> page = new PageImpl<>(List.of(sampleUser));
        Mockito.when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleUser.getId()))
                .andExpect(jsonPath("$.content[0].username").value(sampleUser.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUser_shouldReturnUserById() throws Exception {
        Mockito.when(userService.getUserById(anyLong())).thenReturn(sampleUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleUser.getId()))
                .andExpect(jsonPath("$.username").value(sampleUser.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("test");
        updateDto.setPassword("password");

        UserResponse updatedUser = UserResponse.builder()
                .id(1L)
                .username("updatedTestUsername")
                .password("updatedPassword")
                .build();

        Mockito.when(userService.updateUser(any(UserUpdateDto.class), anyLong())).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedTestUsername"))
                .andExpect(jsonPath("$.password").value("updatedPassword"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getAllUsers_shouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getUser_shouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateUser_shouldBeForbiddenForUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setUsername("test");
        updateDto.setPassword("password");

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deleteUser_shouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}
