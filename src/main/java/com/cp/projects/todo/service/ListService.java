package com.cp.projects.todo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.repo.ListRepo;

@Service
public class ListService {
  @Autowired
  private ListRepo listRepo;

  public List<ToDoList> findAll() {
    return listRepo.findAll();
  }

  public List<ToDoList> getToDoListsbyUserUsername(String username) {
    return listRepo.findByUserUsernameEquals(username);
  }
}
