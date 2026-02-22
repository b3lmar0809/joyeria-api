package com.joyeria.joyeria_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtUtils class
 *
 * @Version: 1.0.0 - 21 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/21
 */
//para generar y validar tokens jwt
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKay;

    @Value("${jwt.expiration}")
    private String Experation;

    //generando token con la weaita de JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    //creando el token con claim
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    // obteniendo token
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKay);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
