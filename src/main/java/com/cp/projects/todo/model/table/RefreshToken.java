package com.cp.projects.todo.model.table;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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

  @Generated(event = EventType.INSERT)
  @Column(insertable = false, nullable = false)
  private LocalDate expireDate;

  @ManyToOne
  @JoinTable(name = "ae_users_to_refresh_tokens", joinColumns = @JoinColumn(name = "refresh_token_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
  private User user;

}
