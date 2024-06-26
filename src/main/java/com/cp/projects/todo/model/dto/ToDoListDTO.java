package com.cp.projects.todo.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.model.table.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ToDoListDTO {
  protected UUID listId;
  protected String title;
  protected String description;
  protected long count;
  protected LocalDateTime created;

  public ToDoListDTO(ToDoList db) {
    this.count = db.getListItemsCount();
    this.description = db.getDescription();
    this.listId = db.getListId();
    this.title = db.getTitle();
    this.created = db.getCreateDate();
  }

  public ToDoList toDBToDoList(User user) {
    return ToDoList.builder()
        .description(description)
        .listId(listId)
        .title(title)
        .user(user)
        .build();
  }
}
