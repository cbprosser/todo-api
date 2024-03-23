package com.cp.projects.todo.filters;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class RequestIDFilter extends OncePerRequestFilter {

  @Override
  @SuppressWarnings("null")
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      final String requestID = UUID.randomUUID().toString();

      response.addHeader("rid", requestID);
      MDC.put("request_id", requestID);

      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

}
