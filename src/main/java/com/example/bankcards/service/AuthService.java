package com.example.bankcards.service;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.security.MyUserDetails;

public interface AuthService {
    String login(AuthRequest authRequest);
    String register(AuthRequest authRequest);
}
