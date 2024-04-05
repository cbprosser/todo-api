package com.cp.projects.todo.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cp.projects.todo.model.table.UserPref;

public interface PrefsRepo extends JpaRepository<UserPref, UUID> {

}
