package com.ken.store.users.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import com.ken.store.common.security.SecurityRules;
import com.ken.store.users.entities.Role;

@Component
public class UserSecurityRules implements SecurityRules {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.GET, "/users/**").hasRole(Role.ADMIN.name()) // Admin can get all users
                .requestMatchers(HttpMethod.POST, "/users").permitAll(); // registerUser() is public
    }

}
