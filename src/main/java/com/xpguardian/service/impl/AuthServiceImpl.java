package com.xpguardian.service.impl;

import com.xpguardian.dto.auth.AuthRequest;
import com.xpguardian.dto.auth.AuthResponse;
import com.xpguardian.dto.auth.RegisterRequest;
import com.xpguardian.entity.Role;
import com.xpguardian.entity.User;
import com.xpguardian.repository.UserRepository;
import com.xpguardian.service.AuthService;
import com.xpguardian.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Lógica para verificar se o usuário já existe pode ser adicionada aqui
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Papel padrão
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // Se chegou aqui, o usuário está autenticado
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // Em um caso real, lançar uma exceção mais específica
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}