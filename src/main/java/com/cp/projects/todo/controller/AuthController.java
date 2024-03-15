package com.cp.projects.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.AuthDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.AuthService;

@RestController
@RequestMapping("auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/")
  public UserDTO findUserByUsernameAndPassword(@RequestBody AuthDTO authDTO) {
    return authService.findUserByUsernameAndPassword(authDTO);
  }

  @PostMapping("/create")
  public ResponseEntity<Void> createUser(@RequestBody User user) throws Exception {
    if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword()))
      return ResponseEntity.badRequest().build();
    authService.createUser(user);
    return ResponseEntity.status(201).build();
  }

}
