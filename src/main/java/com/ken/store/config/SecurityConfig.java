package com.ken.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Traditional: Session + Cookie -> need CSRF
    // Morden RESTful API: Stateless -> don't need CSRF
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Stateless sessions (token-based authentication)
        // Disable CSRF
        // Authorize
        http.sessionManagement(c -> 
                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(c -> c
                .requestMatchers("/carts/**").permitAll() // All requests in "/carts/**" are public
                .requestMatchers("/auth/login").permitAll() // Login is public
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // POST requests in "/users" (registerUser()) are public
                .anyRequest().authenticated() // All other requests are private (only authorized browsers can access it)
        );
        return http.build();
    }

}
