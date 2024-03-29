package com.cp.projects.todo.model.table;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "list_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ToDoListItem {
  
  @Id
  @GeneratedValue
  private UUID listItemId;
  private String description;

  @Column(insertable = false)
  private LocalDateTime createDate;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinTable(name = "ae_lists_to_list_items", joinColumns = {
      @JoinColumn(name = "list_item_id")
  }, inverseJoinColumns = {
      @JoinColumn(name = "list_id")
  })
  @JsonBackReference
  private ToDoList list;
}
