package com.cp.projects.todo.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cp.projects.todo.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
public class JwtUtil {
  private static JwtConfig staticSettings;

  @Autowired
  private JwtConfig settings;

  @PostConstruct
  private void init() {
    staticSettings = this.settings;
  }

  @Getter
  @AllArgsConstructor
  public enum TOKEN_TYPE {
    AUTH((options) -> {
      if (options.payload instanceof String) {
        String payload = (String) options.payload;
        return Jwts.builder()
            .claims()
            .add("type", "AUTH")
            .and()
            .subject(payload)
            .issuedAt(Date.from(options.getIssued().atZone(ZoneId.systemDefault()).toInstant()))
            .expiration(Date.from(options.issued.plus(staticSettings.getAuthExpiration(), ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault()).toInstant()))
            .signWith(options.getKey()).compact();
      }
      return null;
    });

    private Function<JwtTokenOptions<?>, String> jwtGenerator;
  }

  private SecretKey getKey() {
    return Keys
        .hmacShaKeyFor(Base64.getEncoder().encode(settings.getSecret().getBytes(StandardCharsets.UTF_8)));
  }

  public LocalDateTime extractExpiry(String token) {
    return LocalDateTime.ofInstant(extractClaim(token, Claims::getExpiration).toInstant(), ZoneId.systemDefault());
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsFunc) {
    final Claims claims = extractAllClaims(token);
    return claimsFunc.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiry(token).isBefore(LocalDateTime.now());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);

    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public final <T> String create(T payload, TOKEN_TYPE type) {
    final SecretKey key = getKey();

    JwtTokenOptions<T> options = JwtTokenOptions.<T>builder()
        .key(key)
        .payload(payload)
        .issued(LocalDateTime.now())
        .build();

    return type.getJwtGenerator().apply(options);
  }

  public JwtConfig getSettings() {
    return settings;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  private static class JwtTokenOptions<T> {
    private SecretKey key;
    private T payload;
    private LocalDateTime issued;
  }
}