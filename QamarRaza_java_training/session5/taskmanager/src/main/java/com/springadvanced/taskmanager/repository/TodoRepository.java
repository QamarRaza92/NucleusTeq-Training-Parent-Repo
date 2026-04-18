package com.springadvanced.taskmanager.repository;

import com.springadvanced.taskmanager.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoEntity,Long>
{}