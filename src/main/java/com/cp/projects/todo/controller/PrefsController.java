package com.cp.projects.todo.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.service.PrefsService;

@RestController
@RequestMapping("v1/prefs")
public class PrefsController {

  @Autowired
  private PrefsService prefsService;

  @GetMapping("/{username}")
  public ResponseEntity<String> getUsersPrefs(@PathVariable String username) throws Exception {
    return ResponseEntity.ok(prefsService.findUserPrefs(username));
  }

  @PostMapping("/{username}")
  public ResponseEntity<Void> saveUserPrefs(@PathVariable String username, @RequestBody String prefs)
      throws URISyntaxException {
    prefsService.saveUserPrefs(username, prefs);
    URI uri = new URI(String.format("/v1/prefs/%s", username));
    return ResponseEntity.created(uri).build();
  }

}
