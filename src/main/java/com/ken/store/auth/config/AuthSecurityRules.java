package com.ken.store.auth.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import com.ken.store.common.security.SecurityRules;

@Component
public class AuthSecurityRules implements SecurityRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // Login is public
                .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll();
    }

}
