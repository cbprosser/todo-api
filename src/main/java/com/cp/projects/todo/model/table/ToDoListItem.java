package com.cp.projects.todo.model.table;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "list_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDoListItem {
  @Id
  @GeneratedValue
  private UUID listItemId;
  private String description;
  @ManyToOne
  @JoinTable(name = "ae_list_to_list_item", joinColumns = {
      @JoinColumn(name = "list_item_id")
  }, inverseJoinColumns = {
      @JoinColumn(name = "list_id")
  })
  @JsonBackReference
  private ToDoList list;
}
