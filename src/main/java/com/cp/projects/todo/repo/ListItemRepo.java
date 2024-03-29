package com.cp.projects.todo.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cp.projects.todo.model.table.ToDoListItem;

public interface ListItemRepo extends JpaRepository<ToDoListItem, UUID> {
  public void deleteAllByListListId(UUID listListId);
}
