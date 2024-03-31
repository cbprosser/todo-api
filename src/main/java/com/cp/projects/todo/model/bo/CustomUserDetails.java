package com.cp.projects.todo.model.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cp.projects.todo.model.table.User;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CustomUserDetails extends User implements UserDetails {
  private Collection<? extends GrantedAuthority> authorities;
  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean credentialsNonExpired;

  public CustomUserDetails(User user) {
    super(user);
    List<GrantedAuthority> auths = new ArrayList<>();
    auths.add(new SimpleGrantedAuthority("all".toUpperCase()));
    this.authorities = auths;

    this.accountNonExpired = true;
    this.accountNonLocked = true;
    this.credentialsNonExpired = true;
  }
}
