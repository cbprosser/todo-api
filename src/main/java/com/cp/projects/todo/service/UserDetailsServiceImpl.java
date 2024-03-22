package com.cp.projects.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.cp.projects.todo.model.bo.CustomUserDetails;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.repo.SecurityRepo;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private SecurityRepo securityRepo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Loading user {}", username);
    User user = securityRepo.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new CustomUserDetails(user);
  }

}
