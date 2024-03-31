package com.cp.projects.todo.model.table;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verification_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class VerificationToken {

  @Id
  @GeneratedValue
  private UUID verificationTokenId;

  @Builder.Default
  private UUID token = UUID.randomUUID();

  @Builder.Default
  private LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

  @OneToOne
  @JoinColumn(nullable = false, name = "user_id")
  private User user;
}
