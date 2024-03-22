package com.cp.projects.todo.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.AuthDTO;
import com.cp.projects.todo.model.dto.JwtDTO;
import com.cp.projects.todo.model.dto.RefreshTokenDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.RefreshToken;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.AuthService;
import com.cp.projects.todo.service.RefreshTokenService;
import com.cp.projects.todo.util.JwtUtil;
import com.cp.projects.todo.util.JwtUtil.TOKEN_TYPE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("v1/auth")
public class AuthController {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private AuthService authService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private HttpServletRequest request;

  @PostMapping({ "/", "" })
  public UserDTO findUserByUsernameAndPassword(@RequestBody AuthDTO authDTO) throws Exception {
    if (authDTO == null || !StringUtils.hasText(authDTO.getUsername()) || !StringUtils.hasText(authDTO.getPassword()))
      throw new Exception("Missing required authentication properties");
    log.info("Finding user {}", authDTO.getUsername());
    return authService.findUserByUsernameAndPassword(authDTO);
  }

  @PostMapping({ "/create", "/create/" })
  public ResponseEntity<Void> createUser(@RequestBody User user) throws Exception {
    log.info("Create request for user {}", user.getUsername());
    if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())
        || !StringUtils.hasText(user.getEmail()))
      throw new Exception("Missing required authentication properties");
    authService.createUser(user);
    return ResponseEntity.status(201).build();
  }

  @PostMapping({ "/login", "/login/" })
  public ResponseEntity<Void> authenticateAndGetToken(
      @RequestBody AuthDTO authDTO,
      @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
      HttpServletResponse response)
      throws Exception {
    log.info("Login request for user {}", authDTO.getUsername());

    String remoteAddress = request.getRemoteAddr();

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));

    if (authentication.isAuthenticated()) {
      String authToken = jwtUtil.create(authDTO.getUsername(), TOKEN_TYPE.AUTH);
      RefreshToken refreshToken = refreshTokenService
          .ensureRefreshToken(authDTO.getUsername(), userAgent, remoteAddress);

      createCookie("authToken", authToken, jwtUtil.getSettings().getCookieExpiration(), response);
      createCookie(
          "refreshToken",
          refreshToken.getToken(),
          Duration.between(LocalDateTime.now(), refreshToken.getExpireDate().atStartOfDay()).toSeconds(),
          response);

      return ResponseEntity.ok().build();
    }
    throw new UsernameNotFoundException("Invalid user request");
  }

  @PostMapping({ "/refresh", "/refresh/" })
  public ResponseEntity<JwtDTO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
    return ResponseEntity.ok(refreshTokenService.findByToken(refreshTokenDTO.getToken())
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String accessToken = jwtUtil.create(user.getUsername(), TOKEN_TYPE.AUTH);
          return JwtDTO.builder()
              .authToken(accessToken)
              .refreshToken(refreshTokenDTO.getToken())
              .build();
        }).orElseThrow(() -> new RuntimeException("Refresh token not valid")));
  }

  private void createCookie(String cookieName, String cookieContents, long expiration, HttpServletResponse response)
      throws Exception {
    if (cookieName == null || cookieContents == null)
      throw new Exception("Missing cookie name/contents");
    ResponseCookie cookie = ResponseCookie.from(cookieName, cookieContents)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(expiration)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

}
