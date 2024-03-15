package com.cp.projects.todo.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cp.projects.todo.model.table.User;

import jakarta.transaction.Transactional;

public interface AuthRepo extends JpaRepository<User, UUID> {

  @Query(value = "SELECT * FROM {h-schema}user u WHERE u.username = ?1 AND u.password = {h-schema}crypt(?2, u.password)", nativeQuery = true)
  public User findUserByUsernameAndPassword(String username, String password);
  
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO {h-schema}user (username,password,email) VALUES (?1, crypt(?2,gen_salt('md5')), ?3)", nativeQuery = true)
  public int save(String username, String password, String email);
}
