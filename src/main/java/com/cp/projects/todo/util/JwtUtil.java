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
import org.springframework.stereotype.Component;

import com.cp.projects.todo.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
public class JwtUtil {
  @Autowired
  private JwtConfig settings;

  @Getter
  @AllArgsConstructor
  public enum TOKEN_TYPE {
    AUTH((options) -> {
      return Jwts.builder()
          .claims()
          .add("type", "AUTH")
          .add("payload", options.getPayload())
          .and()
          .issuedAt(Date.from(options.getIssued().atZone(ZoneId.systemDefault()).toInstant()))
          .expiration(Date.from(options.getAuthExpiry().atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(options.getKey()).compact();
    }),
    REFRESH((options) -> {
      return Jwts.builder()
          .claims()
          .add("type", "REFRESH")
          .add("payload", options.getPayload())
          .and()
          .issuedAt(Date.from(options.getIssued().atZone(ZoneId.systemDefault()).toInstant()))
          .expiration(Date.from(options.getRefreshExpiry().atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(options.getKey()).compact();
    }),
    PASSWORD_RESET((options) -> {
      return Jwts.builder()
          .claims()
          .add("type", "PASSWORD_RESET")
          .add("payload", options.getPayload())
          .and()
          .issuedAt(Date.from(options.getIssued().atZone(ZoneId.systemDefault()).toInstant()))
          .expiration(Date.from(options.getPasswordResetExpiry().atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(options.getKey()).compact();
    });

    @SuppressWarnings("rawtypes") // TODO: fix this if possible
    private Function<JwtTokenOptions, String> jwtGenerator;
  }

  private SecretKey getKey() {
    return Keys
        .hmacShaKeyFor(Base64.getEncoder().encode(settings.getSecret().getBytes(StandardCharsets.UTF_8)));
  }

  public LocalDateTime extractExpiry(String token) {
    return LocalDateTime.ofInstant(extractClaim(token, Claims::getExpiration).toInstant(), ZoneId.systemDefault());
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsFunc) {
    final Claims claims = extractAllClaims(token);
    return claimsFunc.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().decryptWith(getKey()).build().parseSignedClaims(token).getPayload();
  }

  public final <T> String create(T payload, TOKEN_TYPE type) {
    final SecretKey key = getKey();

    final LocalDateTime issued = LocalDateTime.now();
    final LocalDateTime authExpiry = issued.plus(settings.getAuthExpiration(), ChronoUnit.SECONDS);
    final LocalDateTime passwordRefreshExpiry = issued.plus(settings.getPasswordRefreshExpiration(),
        ChronoUnit.SECONDS);
    final LocalDateTime refreshExpiry = issued.plus(settings.getRefreshExpiration(), ChronoUnit.SECONDS);

    JwtTokenOptions<T> options = JwtTokenOptions.<T>builder()
        .key(key)
        .payload(payload)
        .issued(issued)
        .authExpiry(authExpiry)
        .passwordResetExpiry(passwordRefreshExpiry)
        .refreshExpiry(refreshExpiry)
        .build();

    return type.getJwtGenerator().apply(options);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  private static class JwtTokenOptions<T> {
    private SecretKey key;
    private T payload;
    private LocalDateTime issued;
    private LocalDateTime authExpiry;
    private LocalDateTime passwordResetExpiry;
    private LocalDateTime refreshExpiry;
  }
}
