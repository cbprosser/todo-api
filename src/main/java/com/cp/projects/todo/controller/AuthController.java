package com.cp.projects.todo.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.AuthDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.RefreshToken;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.AuthService;
import com.cp.projects.todo.service.RefreshTokenService;
import com.cp.projects.todo.util.FingerprintUtil;
import com.cp.projects.todo.util.JwtUtil;
import com.cp.projects.todo.util.JwtUtil.TOKEN_TYPE;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("v1/auth")
public class AuthController {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private FingerprintUtil fgpUtil;

  @Autowired
  private AuthService authService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @PostMapping({ "/", "" })
  public UserDTO findUserByUsernameAndPassword(@RequestBody AuthDTO authDTO) throws Exception {
    if (authDTO == null || !StringUtils.hasText(authDTO.getUsername()) || !StringUtils.hasText(authDTO.getPassword()))
      throw new Exception("Missing required authentication properties");
    return authService.findUserByUsernameAndPassword(authDTO);
  }

  @PostMapping({ "/create", "/create/" })
  public ResponseEntity<Void> createUser(@RequestBody User user) throws Exception {
    if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())
        || !StringUtils.hasText(user.getEmail()))
      throw new Exception("Missing required authentication properties");
    authService.createUser(user);
    return ResponseEntity.status(201).build();
  }

  @PostMapping({ "/login", "/login/" })
  public ResponseEntity<Void> authenticateAndGetToken(
      @RequestBody AuthDTO authDTO,
      @CookieValue(name = "fingerprint", required = false) Optional<String> optFingerprintCookie,
      HttpServletResponse response)
      throws Exception {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));

    if (authentication.isAuthenticated()) {
      String fingerprint = optFingerprintCookie.orElse(fgpUtil.getFingerprint());
      String authToken = jwtUtil.create(authDTO.getUsername(), TOKEN_TYPE.AUTH, fingerprint);
      RefreshToken refreshToken = refreshTokenService.ensureRefreshToken(authDTO.getUsername(), fingerprint);

      createCookie("authToken", authToken, jwtUtil.getSettings().getCookieExpiration(), response);
      createCookie("fingerprint", fingerprint, response);
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
  public ResponseEntity<Void> refreshToken(
      @CookieValue(name = "refreshToken") String token,
      @CookieValue String fingerprint,
      HttpServletResponse response)
      throws Exception {
    Optional<RefreshToken> optRefreshToken = refreshTokenService.findByToken(token);

    if (optRefreshToken.isPresent()) {
      RefreshToken refreshToken = optRefreshToken.get();
      String authToken = jwtUtil.create(refreshToken.getUser().getUsername(), TOKEN_TYPE.AUTH, fingerprint);

      createCookie("authToken", authToken, jwtUtil.getSettings().getCookieExpiration(), response);
      createCookie("fingerprint", fingerprint, response);
      createCookie(
          "refreshToken",
          refreshToken.getToken(),
          Duration.between(LocalDateTime.now(), refreshToken.getExpireDate().atStartOfDay()).toSeconds(),
          response);

      return ResponseEntity.ok().build();
    }
    throw new RuntimeException("Invalid refresh token");
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
        .sameSite("strict")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private void createCookie(String cookieName, String cookieContents, HttpServletResponse response)
      throws Exception {
    if (cookieName == null || cookieContents == null)
      throw new Exception("Missing cookie name/contents");
    ResponseCookie cookie = ResponseCookie.from(cookieName, cookieContents)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("strict")
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

}
