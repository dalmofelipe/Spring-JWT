package com.dalmofelipe.SpringJWT.Auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.dalmofelipe.SpringJWT.User.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class TokenService {

    @Value("${jwt.token.expiration}")
    private String expirationTimeInMilis;

    @Value("${jwt.token.secret}")
    private String secret;

    //private static final SecretKey KEY = Jwts.SIG.HS256.key().build();
    private SecretKey MyKEY = null;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJWT(Authentication autheticate) 
    {
        User userLogged = (User) autheticate.getPrincipal();
        Date now = new Date();
        Date expirationTime = 
            new Date(now.getTime() + Long.parseLong(expirationTimeInMilis));
        
        this.MyKEY = getSecretKey();

        String stringJWT = Jwts.builder()
            .issuer("spring-jwt-app")
            .subject(userLogged.getId().toString())
            .claim("roles", userLogged.getAuthorities())
            .issuedAt(now)
            .expiration(expirationTime)
            .signWith(MyKEY)
            .compact();

        this.isTokenValid(stringJWT);

        return stringJWT;
    }

    public Boolean isTokenValid(String tokenWithoutBearer) {
        this.MyKEY = getSecretKey();
        
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(MyKEY).build();
            jwtParser.parseSignedClaims(tokenWithoutBearer);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public Long getSubject(String tokenWithoutBearer) {
        this.MyKEY = getSecretKey();
        
        JwtParser jwtParser = Jwts.parser().verifyWith(MyKEY).build();
        Claims payload = jwtParser.parseSignedClaims(tokenWithoutBearer).getPayload();
        String subject = payload.getSubject();
        return Long.parseLong(subject);
    }

    public void blacklistToken(String token) 
    {
        redisTemplate.opsForValue()
            .set(
                "blacklist:" + token, "true", 
                Long.parseLong(expirationTimeInMilis), 
                java.util.concurrent.TimeUnit.MILLISECONDS
            );
    }

    public boolean isTokenBlacklisted(String token) 
    {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
