package com.cp.projects.todo.model.dto;

import java.util.List;

import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.model.table.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class ToDoListWithItemsDTO extends ToDoListDTO {
  private List<ToDoListItemDTO> items;

  public ToDoListWithItemsDTO(ToDoList db) {
    super(db);
    this.items = db.getItems().stream().map(ToDoListItemDTO::new).toList();
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
