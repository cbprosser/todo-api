package com.cp.projects.todo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.table.ToDoListItem;
import com.cp.projects.todo.repo.ListItemRepo;

@Service
public class ListItemService {
  @Autowired
  private ListItemRepo listItemRepo;

  public List<ToDoListItem> findAll() {
    return listItemRepo.findAll();
  }
}
