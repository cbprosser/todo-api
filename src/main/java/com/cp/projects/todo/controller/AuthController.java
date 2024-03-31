package com.cp.projects.todo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.cp.projects.todo.model.dto.RecoverDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.RefreshToken;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.AuthService;
import com.cp.projects.todo.service.RefreshTokenService;
import com.cp.projects.todo.util.CookieUtils;
import com.cp.projects.todo.util.FingerprintUtil;
import com.cp.projects.todo.util.JwtUtil;
import com.cp.projects.todo.util.JwtUtil.TOKEN_TYPE;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
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

      CookieUtils.createAllCookies(fingerprint, response, refreshToken, authToken);

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

      CookieUtils.createAllCookies(fingerprint, response, refreshToken, authToken);

      return ResponseEntity.ok(new UserDTO(refreshToken.getUser()));
    }
    CookieUtils.deleteAllCookies(response);
    throw new RuntimeException("Invalid refresh token");
  }

  @PostMapping({ "/logout", "/logout/" })
  public ResponseEntity<Void> logout(
      @CookieValue String refreshToken,
      @CookieValue String fingerprint,
      HttpServletResponse response)
      throws Exception {
    refreshTokenService.deleteToken(refreshToken, fingerprint);
    CookieUtils.deleteAllCookies(response);
    return ResponseEntity.ok().build();
  }

  @PostMapping({ "/recover", "/recover/" })
  public ResponseEntity<Void> sendRecoverEntity(@RequestBody RecoverDTO recoverDTO) {
    // authService.sendRecoveryEmail(recoverDTO);
    return ResponseEntity.ok().build();
  }

}
