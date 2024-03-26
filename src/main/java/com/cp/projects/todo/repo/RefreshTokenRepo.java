package com.cp.projects.todo.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cp.projects.todo.model.table.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);
  
  Optional<RefreshToken> findByFingerprintAndUserUsername(String fingerprint, String username);
}
