package com.cp.projects.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.service.ListService;

@RestController
@RequestMapping("v1/lists")
public class ListController {
  @Autowired
  private ListService listService;

  @GetMapping({"/", ""})
  public List<ToDoList> findAll() {
    return listService.findAll();
  }

  @GetMapping({"/{username}", "/{username}/"})
  public List<ToDoList> getToDoListsbyUserUsername(@PathVariable String username) {
    return listService.getToDoListsbyUserUsername(username);
  }

}
