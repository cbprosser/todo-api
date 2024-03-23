package com.cp.projects.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.UserService;

@RestController
@RequestMapping("v1/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping({ "/", "" })
  public List<UserDTO> findAll() throws Exception {
    return userService.findAll();
  }

  @PostMapping({ "/save/", "/save" })
  public ResponseEntity<UserDTO> saveUser(@RequestBody User user) throws Exception {
    return ResponseEntity.ok(userService.saveUser(user));
  }

}
