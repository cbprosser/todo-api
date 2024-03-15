package com.cp.projects.todo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.repo.UserRepo;

@Service
public class UserService {

  @Autowired
  private UserRepo userRepo;

  public List<UserDTO> findAll() {
    return userRepo.findAll().stream().map(db -> new UserDTO(db)).toList();
  }

}
