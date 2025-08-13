package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class UserUpdateDto {
    private String username;
    private String password;
    private Role role;
}
