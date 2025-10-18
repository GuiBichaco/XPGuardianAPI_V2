package com.xpguardian.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpguardian.dto.auth.AuthRequest;
import com.xpguardian.dto.auth.RegisterRequest;
import com.xpguardian.entity.User;
import com.xpguardian.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Limpa o banco de dados após cada teste
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldCreateUserAndReturnToken_whenDataIsValid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("test.user@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue()));

        // Verifica se o usuário foi realmente salvo no DB com senha criptografada
        User savedUser = userRepository.findByEmail("test.user@example.com").orElseThrow();
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("invalid-email") // Email inválido
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void authenticate_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        // 1. Cria um usuário de teste direto no banco
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("auth.test@example.com")
                .password("password123")
                .build();

        // Usamos o endpoint de registro para criar o usuário
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // 2. Tenta autenticar
        AuthRequest authRequest = new AuthRequest("auth.test@example.com", "password123");

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue()));
    }

    @Test
    void authenticate_shouldReturnUnauthorized_whenPasswordIsInvalid() throws Exception {
        // 1. Cria um usuário
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("wrong.pass@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // 2. Tenta autenticar com a senha errada
        AuthRequest authRequest = new AuthRequest("wrong.pass@example.com", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized()) // Espera 401 (do GlobalExceptionHandler)
                .andExpect(jsonPath("$.error").value("Invalid Credentials"));
    }
}