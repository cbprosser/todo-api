package com.cp.projects.todo.model.dto;

import java.time.LocalDateTime;
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
  private LocalDateTime createDate;

  public UserDTO(User db) {
    this.userId = db.getUserId();
    this.username = db.getUsername();
    this.email = db.getEmail();
    this.createDate = db.getCreateDate();
  }
}
