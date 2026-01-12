package com.ken.store.auth.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import com.ken.store.common.security.SecurityRules;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // Spring will automatically initialize this List 
    // it will add all implementations of SecurityRules
    // to this list (it should be annotated as @Component)
    private final List<SecurityRules> featureSecurityRules; 

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
            .authorizeHttpRequests(c -> {
                // list.forEach() is accutually a loop
                // it is faster than list.stream().forEach()
                featureSecurityRules.forEach(rule -> rule.configure(c));
                c.anyRequest().authenticated(); //// All other requests are private (only authorized browsers can access it)
            })
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
