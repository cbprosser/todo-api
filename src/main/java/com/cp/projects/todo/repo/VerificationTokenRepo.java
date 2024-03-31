package com.cp.projects.todo.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cp.projects.todo.model.table.VerificationToken;

public interface VerificationTokenRepo extends JpaRepository<VerificationToken, UUID> {

  Optional<VerificationToken> findByToken(UUID token);

}
