package com.ken.store.config;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Data
public class JwtConfig {
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.accessTokenExpiration}")
    private int accessTokenExpiration;
    @Value("${spring.jwt.refreshTokenExpiration}")
    private int refreshTokenExpiration;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
