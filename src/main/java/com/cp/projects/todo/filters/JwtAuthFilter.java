package com.cp.projects.todo.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cp.projects.todo.constant.UnauthorizedEndpoints;
import com.cp.projects.todo.service.UserDetailsServiceImpl;
import com.cp.projects.todo.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private RequestMatcher ignoredPaths = RequestMatchers.anyOf(UnauthorizedEndpoints.UNAUTHORIZED_ENDPOINT_MATCHERS);

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  UserDetailsServiceImpl userDetailsServiceImpl;

  @Override
  @SuppressWarnings("null")
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    if (ignoredPaths.matches(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    Optional<Cookie> optAuthToken = null;
    Optional<Cookie> optFingerprint = null;

    String authToken = null;
    String fingerprint = null;
    String username = null;
    Map<String, Cookie> cookieMap;

    if (request.getCookies() != null) {
      cookieMap = Arrays.stream(request.getCookies())
          .collect(Collectors.toMap(cookie -> cookie.getName(), cookie -> cookie));
      optAuthToken = Optional.ofNullable(cookieMap.get("authToken"));
      optFingerprint = Optional.ofNullable(cookieMap.get("fingerprint"));
    }

    if (optAuthToken.isPresent() && optFingerprint.isPresent()) {
      authToken = optAuthToken.get().getValue();
      fingerprint = optFingerprint.get().getValue();

      username = jwtUtil.extractUsername(authToken);

      if (username != null) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        if (jwtUtil.isAuthTokenValid(authToken, userDetails.getUsername(), fingerprint)) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
              null, userDetails.getAuthorities());
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } else {
          username = null;
        }
      }
    }

    filterChain.doFilter(request, response);
  }

}
