package com.cp.projects.todo.model.table;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lists")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ToDoList {
  @Id
  @GeneratedValue
  private UUID listId;
  private String title;
  private String description;
  
  @Column(insertable = false)
  private LocalDateTime createDate;
  
  @Formula("(SELECT COUNT(*) FROM ae_lists_to_list_items a WHERE a.list_id = list_id)")
  long listItemsCount;
  
  @ManyToOne
  @JoinTable(name = "ae_users_to_lists", joinColumns = {
      @JoinColumn(name = "list_id")
  }, inverseJoinColumns = {
      @JoinColumn(name = "user_id")
  })
  @JsonBackReference
  private User user;
}
