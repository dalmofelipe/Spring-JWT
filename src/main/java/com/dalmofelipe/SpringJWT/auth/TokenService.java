package com.dalmofelipe.SpringJWT.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.dalmofelipe.SpringJWT.user.User;

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

    private SecretKey MyKEY = null;

    private RedisTemplate<String, String> redisTemplate;


    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJWT(Authentication autheticate) {
        User userLogged = (User) autheticate.getPrincipal();
        Date now = new Date();
        Date expirationTime = new Date(
            now.getTime() + Long.parseLong(expirationTimeInMilis)
        );
        
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

    public void blacklistToken(String tokenWithoutBearer) {
        redisTemplate.opsForValue().set("blacklist:"+tokenWithoutBearer, "true", 
            Long.parseLong(expirationTimeInMilis), java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String tokenWithoutBearer) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:"+tokenWithoutBearer));
    }
}
