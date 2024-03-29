package com.cp.projects.todo.model.dto;

import java.util.UUID;

import com.cp.projects.todo.model.table.ToDoListItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ToDoListItemDTO {
  private UUID listItemId;
  private String description;

  public ToDoListItemDTO(ToDoListItem db) {
    this.description = db.getDescription();
    this.listItemId = db.getListItemId();
  }
}
