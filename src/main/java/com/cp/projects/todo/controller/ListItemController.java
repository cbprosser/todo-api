package com.cp.projects.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.table.ToDoListItem;
import com.cp.projects.todo.service.ListItemService;


@RestController
@RequestMapping(path = "list/item")
public class ListItemController {
  @Autowired
  private ListItemService listItemService;
  
  @GetMapping("/")
  public List<ToDoListItem> findAll() {
      return listItemService.findAll();
  }
  
}
