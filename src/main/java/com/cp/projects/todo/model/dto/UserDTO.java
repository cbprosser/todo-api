package com.cp.projects.todo.model.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.cp.projects.todo.model.table.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {
  private UUID userId;
  private String username;
  private String email;
  private LocalDate joined;

  public UserDTO(User db) {
    this.userId = db.getUserId();
    this.username = db.getUsername();
    this.email = db.getEmail();
    if (db.getCreateDate() != null) {
      this.joined = db.getCreateDate().toLocalDate();
    }
  }
}
