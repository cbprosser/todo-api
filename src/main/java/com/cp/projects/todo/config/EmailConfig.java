package com.cp.projects.todo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
  private String username;

  public String getEmail() {
    return username + "@gmail.com";
  }
}
