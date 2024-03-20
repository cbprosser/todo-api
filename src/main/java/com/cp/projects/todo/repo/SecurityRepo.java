package com.cp.projects.todo.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cp.projects.todo.model.table.User;

public interface SecurityRepo extends JpaRepository<User, UUID> {
  public User findByUsername(String username);
}
