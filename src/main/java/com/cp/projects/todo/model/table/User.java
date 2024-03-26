package com.cp.projects.todo.model.table;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {

  public User(User user) {
    this.userId = user.userId;
    this.username = user.username;
    this.password = user.password;
    this.email = user.email;
    this.createDate = user.createDate;
  }

  @Id
  @GeneratedValue
  private UUID userId;
  private String username;

  @ToString.Exclude
  private String password;
  private String email;

  @Column(insertable = false)
  private LocalDateTime createDate;
}
