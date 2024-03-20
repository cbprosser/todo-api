package com.cp.projects.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.AuthDTO;
import com.cp.projects.todo.model.dto.UserDTO;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.service.AuthService;
import com.cp.projects.todo.util.JwtUtil;
import com.cp.projects.todo.util.JwtUtil.TOKEN_TYPE;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("v1/auth")
public class AuthController {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private AuthService authService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @PostMapping({ "/", "" })
  public UserDTO findUserByUsernameAndPassword(@RequestBody AuthDTO authDTO) throws Exception {
    if (authDTO == null || !StringUtils.hasText(authDTO.getUsername()) || !StringUtils.hasText(authDTO.getPassword()))
      throw new Exception("Missing required authentication properties");
    log.info("Finding user {}", authDTO.getUsername());
    return authService.findUserByUsernameAndPassword(authDTO);
  }

  @PostMapping({ "/create", "/create/" })
  public ResponseEntity<Void> createUser(@RequestBody User user) throws Exception {
    if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())
        || !StringUtils.hasText(user.getEmail()))
      throw new Exception("Missing required authentication properties");
    authService.createUser(user);
    return ResponseEntity.status(201).build();
  }

  @GetMapping({ "/test", "/test/" })
  public ResponseEntity<String> testJWT() {
    log.info("Test");
    return ResponseEntity.ok(jwtUtil.create("test", TOKEN_TYPE.AUTH));
  }

  @PostMapping("/login")
  public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthDTO authDTO) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));
    if (authentication.isAuthenticated()) {
      return ResponseEntity.ok(jwtUtil.generateToken(authDTO.getUsername()));
    }
    throw new UsernameNotFoundException("Invalid user request");
  }

}
