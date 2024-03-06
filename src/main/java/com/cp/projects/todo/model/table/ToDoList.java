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
@Table(name = "list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToDoList {
  @Id
  @GeneratedValue
  private UUID listId;
  private String title;
  private String description;
  @ManyToOne
  @JoinTable(name = "ae_user_to_list", joinColumns = {
      @JoinColumn(name = "list_id")
  }, inverseJoinColumns = {
      @JoinColumn(name = "user_id")
  })
  @JsonBackReference
  private User user;
}
