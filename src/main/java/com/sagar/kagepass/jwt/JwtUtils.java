package com.sagar.kagepass.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.sagar.kagepass.entities.User;
import com.sagar.kagepass.exceptions.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;

@Component
public class JwtUtils {
    @Value("${jwt.expiration}")
    private Long expiration;

    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        secretKey = getSecretKey(secret);
    }

    public String generateToken(User user) {
        System.out.println("\n\n\n\n\n\n\n<-------------------" + secretKey.getEncoded()
                + "-------------------->\n\n\n\n\n\n\n\n");

        return Jwts.builder()
                .claims(Map.of("role", user.getRole().toString()))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean verifyToken(String token, UserDetails user) throws InvalidTokenException {
        if (token != null && StringUtils.isNotEmpty(token) && !isTokenExpired(token)) {
            return user.getUsername().equals(extractUsername(token));
        }
        return false;
    }

    public boolean verifySignature(String token) throws InvalidTokenException {
        return token != null && StringUtils.isNotEmpty(token) && !isTokenExpired(token);
    }

    public SecretKey getSecretKey(String secret) {
        var decodedSecret = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(decodedSecret);
    }

    private boolean isTokenExpired(String token) throws InvalidTokenException {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) throws InvalidTokenException {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws InvalidTokenException {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws InvalidTokenException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws InvalidTokenException {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new InvalidTokenException(ex.getMessage(), ex.getCause());
        }
    }

}
