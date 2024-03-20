package com.cp.projects.todo.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cp.projects.todo.model.table.User;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

  User findByUsername(String username);
}
