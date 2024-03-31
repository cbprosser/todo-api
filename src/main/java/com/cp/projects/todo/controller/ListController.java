package com.cp.projects.todo.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.ToDoListDTO;
import com.cp.projects.todo.service.ListService;

@RestController
@RequestMapping("v1/lists")
public class ListController {
  @Autowired
  private ListService listService;

  @GetMapping({ "/{username}", "/{username}/" })
  public ResponseEntity<List<ToDoListDTO>> getToDoListsbyUserUsername(@PathVariable String username) {
    return ResponseEntity.ok(listService.getToDoListsbyUserUsername(username));
  }

  @GetMapping("/{username}/{listId}")
  public ResponseEntity<ToDoListDTO> getToDoListById(@PathVariable UUID listId) {
    return ResponseEntity.ok(listService.getToDoListById(listId));
  }

  @PostMapping("/{username}/add")
  public ResponseEntity<ToDoListDTO> addNewList(@RequestBody ToDoListDTO newList, @PathVariable String username)
      throws URISyntaxException {
    ToDoListDTO createdList = listService.addNewList(newList, username);
    URI uri = new URI(String.format("/v1/lists/%s/%s", username, createdList.getListId()));
    return ResponseEntity.created(uri).body(createdList);
  }

  @PostMapping("/{username}/update")
  public ResponseEntity<ToDoListDTO> updateList(@RequestBody ToDoListDTO newList, @PathVariable String username)
      throws Exception {
    ToDoListDTO createdList = listService.updateList(newList, username);
    URI uri = new URI(String.format("/v1/lists/%s/%s", username, createdList.getListId()));
    return ResponseEntity.created(uri).body(createdList);
  }

  @GetMapping("/{username}/{listId}/delete")
  public ResponseEntity<Void> deleteList(@PathVariable UUID listId, @PathVariable String username) {
    listService.deleteList(listId, username);
    return ResponseEntity.ok().build();
  }

}
