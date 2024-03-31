package com.cp.projects.todo.model.table;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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

  @Id
  @GeneratedValue
  private UUID userId;
  protected String username;

  @ToString.Exclude
  protected String password;
  private String email;

  @Generated(event = EventType.INSERT)
  @Column(insertable = false)
  private LocalDateTime createDate;
  
  protected boolean enabled;

  public User(User user) {
    this.userId = user.userId;
    this.username = user.username;
    this.password = user.password;
    this.email = user.email;
    this.createDate = user.createDate;
    this.enabled = user.enabled;
  }

}
