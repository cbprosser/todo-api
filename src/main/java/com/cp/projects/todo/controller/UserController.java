package com.cp.projects.todo.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.UserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("v1/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/{userID}")
  public ResponseEntity<UserDTO> getUserByID(@PathVariable UUID userID) {
    return ResponseEntity.ok(userService.getUserByID(userID));
  }

  @PostMapping({ "/save/", "/save" })
  public ResponseEntity<Void> saveUser(@RequestBody User user) throws Exception {
    log.info("Saving user {}", user);
    UserDTO newUser = userService.saveUser(user);
    URI uri = new URI(String.format("/v1/users/%s", newUser.getUserId()));
    return ResponseEntity.created(uri).build();
  }
  
  @GetMapping({ "/confirm/{token}", "/confirm/{token}/" })
  public ResponseEntity<Void> confirmEmail(@PathVariable UUID token) throws Exception {
    userService.verifyUser(token);
    return ResponseEntity.ok().build();
  }

}
