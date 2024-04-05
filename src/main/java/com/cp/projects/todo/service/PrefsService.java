package com.cp.projects.todo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.model.table.UserPref;
import com.cp.projects.todo.repo.PrefsRepo;
import com.cp.projects.todo.repo.UserRepo;

@Service
public class PrefsService {

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private PrefsRepo prefsRepo;

  @SuppressWarnings("null")
  public String findUserPrefs(String username) throws Exception {
    User user = userRepo.findByUsername(username);
    Optional<UserPref> prefs = prefsRepo.findById(user.getUserId());
    if (!prefs.isPresent()) {
      throw new Exception("User has no prefs saved");
    }
    return prefs.get().getPrefs().toString();
  }

  @SuppressWarnings("null")
  public void saveUserPrefs(String username, String prefs) {
    User user = userRepo.findByUsername(username);
    prefsRepo.save(UserPref.builder().userId(user.getUserId()).prefs(prefs).build());
  }

}
