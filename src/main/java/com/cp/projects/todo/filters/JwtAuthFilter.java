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
import lombok.extern.log4j.Log4j2;

@Log4j2
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
    log.info("{} - {}", request.getMethod(), request.getRequestURI());
    if (ignoredPaths.matches(request)) {
      filterChain.doFilter(request, response);
      return;
    }
    log.info("Filtering");

    Optional<Cookie> optAuthToken = Optional.empty();
    Optional<Cookie> optFingerprint = Optional.empty();

    if (request.getCookies() != null) {
      Map<String, Cookie> cookieMap = Arrays.stream(request.getCookies())
          .collect(Collectors.toMap(cookie -> cookie.getName(), cookie -> cookie));
      optAuthToken = Optional.ofNullable(cookieMap.get("authToken"));
      optFingerprint = Optional.ofNullable(cookieMap.get("fingerprint"));
    }

    if (optAuthToken.isPresent() && optFingerprint.isPresent()) {
      String authToken = null;
      String fingerprint = null;
      String username = null;
      authToken = optAuthToken.get().getValue();
      fingerprint = optFingerprint.get().getValue();
      log.debug("AuthToken: {}\nFingerprint: {}", authToken, fingerprint);

      username = jwtUtil.extractUsername(authToken);

      if (username != null) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        if (jwtUtil.isAuthTokenValid(authToken, userDetails.getUsername(), fingerprint)) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
              null, userDetails.getAuthorities());
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);

          filterChain.doFilter(request, response);
          return;
        }
      }
    }

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

}
