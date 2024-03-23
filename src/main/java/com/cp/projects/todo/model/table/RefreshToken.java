package com.cp.projects.todo.model.table;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RefreshToken {
  @Id
  @GeneratedValue
  private UUID refreshTokenId;
  private String token;
  private String fingerprint;
  @Column(insertable = false)
  private LocalDate expireDate;

  @ManyToOne
  @JoinTable(name = "ae_users_to_refresh_tokens", joinColumns = @JoinColumn(name = "refresh_token_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private User user;

}
