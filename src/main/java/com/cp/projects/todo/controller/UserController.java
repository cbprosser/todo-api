package com.cp.projects.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.service.UserService;

@RestController
@RequestMapping("user")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/")
  public List<UserDTO> findAll() {
    return userService.findAll();
  }
}
