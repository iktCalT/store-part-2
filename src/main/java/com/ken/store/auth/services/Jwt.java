package com.ken.store.auth.services;

import java.util.Date;
import javax.crypto.SecretKey;
import com.ken.store.users.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Jwt {

    private final Claims claims;
    private final SecretKey key;


    // isExpired = not vaild
    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId() {
        return Long.valueOf(claims.getSubject());
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    // Generate token
    @Override
    public String toString() {
        return Jwts.builder().claims(claims).signWith(key).compact();
    }
}
