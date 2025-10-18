package com.xpguardian.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer; // Importação para o H2
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    // Esta lista CONTÉM as URLs do Swagger e do H2
    private static final String[] WHITE_LIST_URLS = {
            "/",
            "/api/v1/auth/**",
            "/v3/api-docs/**",   // Essencial para o Swagger
            "/swagger-ui/**",   // Essencial para o Swagger
            "/h2-console/**"    // Essencial para o H2
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(req ->
                        // VAMOS LIBERAR TUDO APENAS PARA TESTAR
                        req.anyRequest().permitAll()
                );

        // Note que removemos o .sessionManagement, .authenticationProvider, e o .addFilterBefore
        // Isso é apenas um teste de diagnóstico.

        return http.build();
    }
}