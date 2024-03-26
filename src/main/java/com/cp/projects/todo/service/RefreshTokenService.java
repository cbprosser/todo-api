package com.cp.projects.todo.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.table.RefreshToken;
import com.cp.projects.todo.repo.RefreshTokenRepo;
import com.cp.projects.todo.repo.UserRepo;
import com.cp.projects.todo.util.FingerprintUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RefreshTokenService {

  @Autowired
  private RefreshTokenRepo refreshTokenRepo;

  @Autowired
  private UserRepo userRepo;
  
  @Autowired
  private FingerprintUtil fgpUtil;

  @SuppressWarnings("null")
  private RefreshToken createRefreshToken(String username) {
    RefreshToken refreshToken = RefreshToken.builder()
        .user(userRepo.findByUsername(username))
        .token(UUID.randomUUID().toString())
        .fingerprint(fgpUtil.getFingerprint())
        .build();
    RefreshToken saved = refreshTokenRepo.save(refreshToken);
    return saved;
  }

  @SuppressWarnings("null")
  public RefreshToken ensureRefreshToken(String username, String fingerprint) {
    Optional<RefreshToken> existingToken = refreshTokenRepo.findByFingerprintAndUserUsername(fingerprint, username);
    if (existingToken.isPresent()) {
      log.info("existingToken: {}", existingToken.get());
      RefreshToken token = existingToken.get();
      try {
        return verifyExpiration(token);
      } catch (RuntimeException e) {
        refreshTokenRepo.delete(token);
      }
    }
    return createRefreshToken(username);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepo.findByToken(token);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpireDate().isBefore(LocalDate.now())) {
      refreshTokenRepo.delete(token);
      throw new RuntimeException(token.getToken() + " Refresh token expired.");
    }
    return token;
  }

  public RefreshToken verifyUser(RefreshToken token, String username) {
    if (!token.getUser().getUsername().equals(username)) {
      refreshTokenRepo.delete(token);
      throw new RuntimeException(token.getToken() + " Refresh token does not match expected user.");
    }
    return token;
  }
}
