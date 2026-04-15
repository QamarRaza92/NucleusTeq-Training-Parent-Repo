package com.springadvanced.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springadvanced.taskmanager.entity.Todo;
public interface TodoRepository extends JpaRepository<Todo, Long>
{
}