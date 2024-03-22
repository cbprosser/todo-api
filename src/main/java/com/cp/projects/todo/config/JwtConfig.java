package com.cp.projects.todo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
  private String secret;
  @Value("${jwt.expiration.auth}")
  private long authExpiration;
  @Value("${jwt.expiration.passwordReset}")
  private long passwordRefreshExpiration;
  @Value("${jwt.expiration.refresh}")
  private long refreshExpiration;
  @Value("${jwt.expiration.cookie}")
  private long cookieExpiration;
  private String prefix;
  private String header;
}
