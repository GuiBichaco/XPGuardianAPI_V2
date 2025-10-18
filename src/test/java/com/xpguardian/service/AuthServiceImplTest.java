package com.xpguardian.service;

import com.xpguardian.dto.auth.AuthRequest;
import com.xpguardian.dto.auth.AuthResponse;
import com.xpguardian.dto.auth.RegisterRequest;
import com.xpguardian.entity.Role;
import com.xpguardian.entity.User;
import com.xpguardian.repository.UserRepository;
import com.xpguardian.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .build();

        authRequest = AuthRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_shouldSaveUserAndReturnToken() {
        // Given (Mockar as dependências)
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");

        // O `userRepository.save()` vai retornar o usuário salvo (que é o que seria esperado)
        // Usamos `any(User.class)` porque a instância criada dentro do método é diferente
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When (Executar o método)
        AuthResponse response = authService.register(registerRequest);

        // Then (Verificar o resultado)
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockJwtToken");

        // Verifica se os métodos mockados foram chamados
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getEmail().equals("test@example.com") &&
                        savedUser.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void authenticate_shouldAuthenticateAndReturnToken() {
        // Given
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        )).thenReturn(null); // O AuthManager não retorna nada se for sucesso, só lança exceção se falhar

        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockJwtToken");

        // When
        AuthResponse response = authService.authenticate(authRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockJwtToken");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenAuthFails() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.authenticate(authRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        // Verifica que o userRepository e o jwtService nem foram chamados
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}