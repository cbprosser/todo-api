package com.cp.projects.todo.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.model.table.ToDoListItem;
import com.cp.projects.todo.model.table.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDoListDTO {
  private UUID listId;
  private String title;
  private String description;
  private long count;
  private LocalDateTime created;
  private List<ToDoListItem> items;

  public ToDoListDTO(ToDoList db) {
    this.count = db.getListItemsCount();
    this.description = db.getDescription();
    this.listId = db.getListId();
    this.title = db.getTitle();
    this.created = db.getCreateDate();
    this.items = db.getItems();
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
