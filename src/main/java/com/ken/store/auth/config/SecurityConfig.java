package com.ken.store.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.ken.store.auth.filters.JwtAuthenticationFilter;
import com.ken.store.users.entities.Role;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Traditional: Session + Cookie -> need CSRF
    // Morden RESTful API: Stateless -> don't need CSRF
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Stateless sessions (token-based authentication)
        // Disable CSRF
        // Authorize
        http
            .sessionManagement(c -> 
                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(c -> c
                .requestMatchers("/").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").hasRole(Role.ADMIN.name())
                .requestMatchers("/carts/**").permitAll() // All requests in "/carts/**" are public
                .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/users/**").hasRole(Role.ADMIN.name()) // Admin can get all users
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // registerUser() is public
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // Login is public
                .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.POST, "/checkout/webhook").permitAll() // stripe / paypal will not login as a user, we have to make this endpoint public 
                .requestMatchers(HttpMethod.GET, "/payment-result/**").permitAll()
                .anyRequest().authenticated() // All other requests are private (only authorized browsers can access it)
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(c -> {
                // If authentication doesn't pass return 401 UNAUTHORIZED by default
                c.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                // If request is denied, return 403 FORBIDEN by default
                c.accessDeniedHandler(
                    (request, response, accessDeniedHandler) 
                    -> response.setStatus(HttpStatus.FORBIDDEN.value()));
            });
        return http.build();
    }

}
