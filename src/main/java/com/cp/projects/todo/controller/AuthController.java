package com.cp.projects.todo.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
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
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("v1/auth")
public class AuthController {

  private enum COOKIE_TYPE {
    SECURE,
    UNSECURE,
  }

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
  public ResponseEntity<UserDTO> authenticateAndGetToken(
      @RequestBody AuthDTO authDTO,
      @CookieValue(name = "fingerprint", required = false) Optional<String> optFingerprintCookie,
      HttpServletResponse response)
      throws Exception {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));

    if (authentication.isAuthenticated()) {
      log.info("Details: {}\nPrincipal: {}", authentication.getDetails(), authentication.getPrincipal());
      String fingerprint = optFingerprintCookie.orElse(fgpUtil.getFingerprint());
      RefreshToken refreshToken = refreshTokenService.ensureRefreshToken(authDTO.getUsername(), fingerprint);
      if (!fingerprint.equals(refreshToken.getFingerprint())) {
        fingerprint = refreshToken.getFingerprint();
      }
      String authToken = jwtUtil.create(authDTO.getUsername(), TOKEN_TYPE.AUTH, fingerprint);

      createCookie("authToken", authToken, jwtUtil.getSettings().getAuthExpiration(), response, COOKIE_TYPE.SECURE);
      createCookie("fingerprint",
          fingerprint,
          LocalDateTime.of(
              9999,
              12,
              31,
              11,
              59,
              59,
              999).toEpochSecond(ZoneOffset.UTC),
          response,
          COOKIE_TYPE.SECURE);
      createCookie("hasfgpt",
          "",
          LocalDateTime.of(
              9999,
              12,
              31,
              11,
              59,
              59,
              999).toEpochSecond(ZoneOffset.UTC),
          response,
          COOKIE_TYPE.UNSECURE);
      createCookie(
          "refreshToken",
          refreshToken.getToken(),
          Duration.between(LocalDateTime.now(), refreshToken.getExpireDate().atStartOfDay()).toSeconds(),
          response,
          COOKIE_TYPE.SECURE);

      return ResponseEntity.ok(new UserDTO((User) authentication.getPrincipal()));
    }
    throw new UsernameNotFoundException("Invalid user request");
  }

  @PostMapping({ "/refresh", "/refresh/" })
  public ResponseEntity<UserDTO> refreshToken(
      @CookieValue(name = "refreshToken") String token,
      @CookieValue String fingerprint,
      HttpServletResponse response)
      throws Exception {
    Optional<RefreshToken> optRefreshToken = refreshTokenService.findByToken(token);

    if (optRefreshToken.isPresent()) {
      RefreshToken refreshToken = optRefreshToken.get();
      String authToken = jwtUtil.create(refreshToken.getUser().getUsername(), TOKEN_TYPE.AUTH, fingerprint);

      createCookie("authToken", authToken, jwtUtil.getSettings().getAuthExpiration(), response, COOKIE_TYPE.SECURE);
      createCookie("fingerprint",
          fingerprint,
          LocalDateTime.of(
              9999,
              12,
              31,
              11,
              59,
              59,
              999).toEpochSecond(ZoneOffset.UTC),
          response,
          COOKIE_TYPE.SECURE);
      createCookie("hasfgpt",
          "",
          LocalDateTime.of(
              9999,
              12,
              31,
              11,
              59,
              59,
              999).toEpochSecond(ZoneOffset.UTC),
          response,
          COOKIE_TYPE.UNSECURE);
      createCookie(
          "refreshToken",
          refreshToken.getToken(),
          Duration.between(LocalDateTime.now(), refreshToken.getExpireDate().atStartOfDay()).toSeconds(),
          response,
          COOKIE_TYPE.SECURE);

      return ResponseEntity.ok(new UserDTO(refreshToken.getUser()));
    }
    throw new RuntimeException("Invalid refresh token");
  }

  private void createCookie(
      String cookieName,
      String cookieContents,
      long expiration,
      HttpServletResponse response,
      COOKIE_TYPE type)
      throws Exception {
    if (cookieName == null || cookieContents == null)
      throw new Exception("Missing cookie name/contents");
    ResponseCookieBuilder cookieBuilder = ResponseCookie.from(cookieName, cookieContents)
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(expiration)
        .sameSite("strict");
    switch (type) {
      case UNSECURE:
        cookieBuilder.httpOnly(false);
        break;
      default:
        cookieBuilder.httpOnly(true);
        break;
    }
    ResponseCookie cookie = cookieBuilder.build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

}
