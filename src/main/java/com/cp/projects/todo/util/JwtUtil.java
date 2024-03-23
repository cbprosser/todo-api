package com.cp.projects.todo.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.cp.projects.todo.config.JwtConfig;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class JwtUtil {
  private static JwtConfig staticSettings;

  @Autowired
  private JwtConfig settings;

  @Autowired
  private FingerprintUtil fgpUtil;

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
        return JWT.create()
            .withClaim("type", "AUTH")
            .withClaim("fingerprint", options.getFingerprint())
            .withSubject(payload)
            .withIssuedAt(Date.from(options.getIssued().atZone(ZoneId.systemDefault()).toInstant()))
            .withExpiresAt(Date.from(options.issued.plus(staticSettings.getAuthExpiration(), ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault()).toInstant()))
            .sign(getAlgorithm());
      }
      return null;
    }),
    REFRESH((options) -> {
      if (options.payload instanceof String) {
        String payload = (String) options.payload;
        return JWT.create()
            .withClaim("type", "REFRESH")
            .withClaim("fingerprint", options.getFingerprint())
            .withClaim("token", payload)
            .withIssuedAt(Date.from(options.getIssued().atZone(ZoneId.systemDefault()).toInstant()))
            .withExpiresAt(Date.from(options.issued.plus(staticSettings.getAuthExpiration(), ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault()).toInstant()))
            .sign(getAlgorithm());
      }
      return null;
    });

    private Function<JwtTokenOptions<?>, String> jwtGenerator;
  }

  private static Algorithm getAlgorithm() {
    return Algorithm.HMAC256(staticSettings.getSecret());
  }

  public String extractUsername(String token) {
    return JWT.decode(token).getSubject();
  }

  public boolean isAuthTokenValid(String token, String username, String fingerprint) {
    try {
      JWTVerifier verifier = JWT.require(getAlgorithm())
          .withClaim("fingerprint", fgpUtil.getEncryptedFingerprint(fingerprint))
          .withClaim("type", TOKEN_TYPE.AUTH.toString())
          .withSubject(username)
          .build();

      return isTokenValid(token, verifier);
    } catch (IllegalArgumentException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
      log.error("JWT validation error", e);
      return false;
    }
  }

  public boolean isRefreshTokenValid(String token, String refreshToken, String fingerprint) {
    try {
      JWTVerifier verifier = JWT.require(getAlgorithm())
          .withClaim("fingerprint", fgpUtil.getEncryptedFingerprint(fingerprint))
          .withClaim("type", TOKEN_TYPE.REFRESH.toString())
          .withClaim("token", refreshToken)
          .build();

      return isTokenValid(token, verifier);
    } catch (IllegalArgumentException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
      log.error("JWT validation error", e);
      return false;
    }
  }

  private boolean isTokenValid(String token, JWTVerifier verifier) {
    verifier.verify(token);
    return true;
  }

  public final <T> String create(T payload, TOKEN_TYPE type) {
    JwtTokenOptions<T> options = JwtTokenOptions.<T>builder()
        .payload(payload)
        .issued(LocalDateTime.now())
        .build();

    return type.getJwtGenerator().apply(options);
  }

  public final <T> String create(T payload, TOKEN_TYPE type, String fingerprint)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    JwtTokenOptions<T> options = JwtTokenOptions.<T>builder()
        .payload(payload)
        .issued(LocalDateTime.now())
        .fingerprint(fgpUtil.getEncryptedFingerprint(fingerprint))
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
    private T payload;
    private LocalDateTime issued;
    private String fingerprint;
  }
}