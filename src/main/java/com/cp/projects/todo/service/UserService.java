package com.cp.projects.todo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.repo.UserRepo;

@Service
public class UserService {

  @Autowired
  private UserRepo userRepo;

  public List<UserDTO> findAll() {
    return userRepo.findAll().stream().map(db -> new UserDTO(db)).toList();
  }

  @SuppressWarnings("null")
  public UserDTO saveUser(User user) throws Exception {
    if (user == null) {
      throw new Exception("User missing");
    }
    User savedUser = null;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String passRaw = user.getPassword();
    String passEncoded = encoder.encode(passRaw);

    User encodedUser = user.toBuilder().password(passEncoded).build();
    savedUser = userRepo.save(encodedUser);

    return new UserDTO(savedUser);
  }

}
