package com.cp.projects.todo.constant;

import java.util.Arrays;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class UnauthorizedEndpoints {
  private final static String[] UNAUTHORIZED_ENDPOINTS_WITHOUT_SLASHES = new String[] {
      "/v1/auth/login",
      "/v1/auth/refresh",
      "/v1/users/save"
  };
  public final static String[] UNAUTHORIZED_ENDPOINTS = Arrays.stream(UNAUTHORIZED_ENDPOINTS_WITHOUT_SLASHES)
      .map(endpoint -> new String[] { endpoint, endpoint + "/" })
      .flatMap(pair -> Arrays.stream(pair))
      .toArray(String[]::new);

  public final static RequestMatcher[] UNAUTHORIZED_ENDPOINT_MATCHERS = Arrays.stream(UNAUTHORIZED_ENDPOINTS)
      .map(AntPathRequestMatcher::new).toArray(AntPathRequestMatcher[]::new);
}
