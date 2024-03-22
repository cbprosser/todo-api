package com.cp.projects.todo.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    log.debug("Filtering request for endpoint {}", request.getRequestURI());
    String authToken = null;
    String refreshToken = null;
    String username = null;
    Map<String, Cookie> cookieMap;

    if (request.getCookies() != null) {
      cookieMap = Arrays.stream(request.getCookies())
          .collect(Collectors.toMap(cookie -> cookie.getName(), cookie -> cookie));
      authToken = cookieMap.get("authToken").getValue();
      refreshToken = cookieMap.get("refreshToken").getValue();
      log.info("authToken: {}\nrefreshToken: {}", authToken, refreshToken);
    }

    if (authToken != null) {
      username = jwtUtil.extractUsername(authToken);

      if (username != null) {
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        if (jwtUtil.isTokenValid(authToken, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
              null, userDetails.getAuthorities());
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }

}
