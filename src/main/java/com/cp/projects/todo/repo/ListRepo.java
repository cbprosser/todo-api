package com.cp.projects.todo.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cp.projects.todo.model.table.ToDoList;

public interface ListRepo extends JpaRepository<ToDoList, UUID> {

  List<ToDoList> findByUserUsernameEquals(String username);
  
}
