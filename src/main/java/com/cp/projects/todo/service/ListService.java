package com.cp.projects.todo.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cp.projects.todo.model.dto.ToDoListDTO;
import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.model.table.User;
import com.cp.projects.todo.repo.ListItemRepo;
import com.cp.projects.todo.repo.ListRepo;
import com.cp.projects.todo.repo.UserRepo;

@Service
public class ListService {
  @Autowired
  private ListRepo listRepo;

  @Autowired
  private ListItemRepo listItemRepo;

  @Autowired
  private UserRepo userRepo;

  public List<ToDoListDTO> findAll() {
    return listRepo.findAll().stream().map(ToDoListDTO::new).toList();
  }

  public List<ToDoListDTO> getToDoListsbyUserUsername(String username) {
    return listRepo.findByUserUsernameEquals(username).stream().map(ToDoListDTO::new).toList();
  }

  @SuppressWarnings("null")
  public ToDoListDTO getToDoListById(UUID listId) {
    return new ToDoListDTO(listRepo.findById(listId).get());
  }

  @SuppressWarnings("null")
  public ToDoListDTO addNewList(ToDoListDTO newList, String username) {
    User listUser = userRepo.findByUsername(username);
    return new ToDoListDTO(listRepo.save(newList.toDBToDoList(listUser)));
  }

  @SuppressWarnings("null")
  public ToDoListDTO updateList(ToDoListDTO newList, String username) throws Exception {
    Optional<ToDoList> oldList = listRepo.findById(newList.getListId());
    if (!oldList.isPresent()) {
      throw new Exception("List not found");
    }
    if (!oldList.get().getUser().getUsername().equals(username)) {
      throw new Exception("Username does not match list user");

    }
    return new ToDoListDTO(listRepo
        .save(oldList.get().toBuilder().title(newList.getTitle()).description(newList.getDescription()).build()));
  }

  @Transactional
  @SuppressWarnings("null")
  public void deleteList(UUID listId, String username) {
    listItemRepo.deleteAllByListListId(listId);
    listRepo.delete(ToDoList.builder().listId(listId).build());
  }
}
