package com.xpguardian.service;

import com.xpguardian.dto.auth.AuthRequest;
import com.xpguardian.dto.auth.AuthResponse;
import com.xpguardian.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}