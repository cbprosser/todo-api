package com.cp.projects.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.dto.AuthDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.repo.AuthRepo;

@Service
public class AuthService {

  @Autowired
  private AuthRepo authRepo;

  public UserDTO findUserByUsernameAndPassword(AuthDTO authDTO) {
    User authorizedUser = authRepo.findUserByUsernameAndPassword(authDTO.getUsername(), authDTO.getPassword());
    return new UserDTO(authorizedUser);
  }

  public boolean insertUser(User user) {
    if (user == null)
      return false;
    authRepo.save(user);
    return true;
  }

  public void createUser(@NonNull User user) {
    authRepo.save(user.getUsername(), user.getPassword(), user.getEmail());
  }
}
