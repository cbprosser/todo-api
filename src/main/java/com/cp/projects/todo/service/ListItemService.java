package com.cp.projects.todo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cp.projects.todo.model.dto.ToDoListItemDTO;
import com.cp.projects.todo.model.dto.ToDoListWithItemsDTO;
import com.cp.projects.todo.model.table.ToDoList;
import com.cp.projects.todo.model.table.ToDoListItem;
import com.cp.projects.todo.repo.ListItemRepo;
import com.cp.projects.todo.repo.ListRepo;

@Service
public class ListItemService {
  @Autowired
  private ListItemRepo listItemRepo;

  @Autowired
  private ListRepo listRepo;

  @SuppressWarnings("null")
  public ToDoListWithItemsDTO getListWithItems(String username, UUID listID) throws Exception {
    Optional<ToDoList> optList = listRepo.findById(listID);
    if (!optList.isPresent())
      throw new Exception("List not found");
    ToDoList list = optList.get();
    if (!list.getUser().getUsername().equals(username))
      throw new Exception("Username doesn't match list.");
    list.getItems();
    return new ToDoListWithItemsDTO(list);
  }

  @SuppressWarnings("null")
  public void addItemToList(ToDoListItemDTO item, UUID listID, String username) throws Exception {
    Optional<ToDoList> optExistingList = listRepo.findById(listID);
    if (!optExistingList.isPresent())
      throw new Exception("ListId doesn't match");
    if (!optExistingList.get().getUser().getUsername().equals(username))
      throw new Exception("Username doesn't match list.");
    ToDoListItem newItem = ToDoListItem.builder()
        .listItemId(item.getListItemId())
        .description(item.getDescription())
        .list(optExistingList.get())
        .build();
    listItemRepo.save(newItem);
  }

  @SuppressWarnings("null")
  public void updateItemInList(ToDoListItemDTO newItem, UUID listID, String username) throws Exception {
    Optional<ToDoList> optFoundList = listRepo.findById(listID);
    if (!optFoundList.isPresent())
      throw new Exception("ListId doesn't match");
    ToDoList foundList = optFoundList.get();
    if (!foundList.getUser().getUsername().equals(username))
      throw new Exception("Username doesn't match list.");
    Optional<ToDoListItem> oldItem = foundList.getItems().stream()
        .filter(item -> item.getListItemId().equals(newItem.getListItemId())).findFirst();
    if (!oldItem.isPresent())
      throw new Exception("Item not in found list");
    listItemRepo.save(oldItem.get().toBuilder().description(newItem.getDescription()).build());
  }

  @SuppressWarnings("null")
  public void deleteListItem(String username, UUID listID, UUID listItemID) throws Exception {
    Optional<ToDoList> optFoundList = listRepo.findById(listID);
    if (!optFoundList.isPresent())
      throw new Exception("ListId doesn't match");
    ToDoList foundList = optFoundList.get();
    if (!foundList.getUser().getUsername().equals(username))
      throw new Exception("Username doesn't match list.");
    if (!foundList.getItems().stream().anyMatch(item -> item.getListItemId().equals(listItemID)))
      throw new Exception("Item not in found list");
    listItemRepo.deleteById(listItemID);
  }
}
