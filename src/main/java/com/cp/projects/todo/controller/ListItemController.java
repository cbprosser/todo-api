package com.cp.projects.todo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.dto.ToDoListItemDTO;
import com.cp.projects.todo.model.dto.ToDoListWithItemsDTO;
import com.cp.projects.todo.model.table.ToDoListItem;
import com.cp.projects.todo.service.ListItemService;

@RestController
@RequestMapping("v1/lists/items")
public class ListItemController {
  @Autowired
  private ListItemService listItemService;

  @GetMapping({ "/{username}/{listID}", "/{username}/{listID}/" })
  public ResponseEntity<ToDoListWithItemsDTO> getFullListWithItems(@PathVariable String username,
      @PathVariable UUID listID)
      throws Exception {
    return ResponseEntity.ok(listItemService.getListWithItems(username, listID));
  }

  @PostMapping({ "/{username}/{listID}/add", "/{username}/{listID}/add/" })
  public ResponseEntity<ToDoListItem> addItemToList(
      @PathVariable String username,
      @PathVariable UUID listID,
      @RequestBody ToDoListItemDTO item)
      throws Exception {
    listItemService.addItemToList(item, listID, username);
    return null;
  }

  @PostMapping({ "/{username}/{listID}/update", "/{username}/{listID}/update/" })
  public ResponseEntity<ToDoListItem> updateItemInList(
      @PathVariable String username,
      @PathVariable UUID listID,
      @RequestBody ToDoListItemDTO item)
      throws Exception {
    listItemService.updateItemInList(item, listID, username);
    return null;
  }

  @GetMapping({ "/{username}/{listID}/{listItemID}/delete", "/{username}/{listID}/{listItemID}/delete/" })
  public ResponseEntity<Void> deleteListItem(
      @PathVariable String username,
      @PathVariable UUID listID,
      @PathVariable UUID listItemID)
      throws Exception {
    listItemService.deleteListItem(username, listID, listItemID);
    return ResponseEntity.ok().build();
  }

}
