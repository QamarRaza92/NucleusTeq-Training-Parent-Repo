package com.springadvanced.taskmanager.dto;

import com.springadvanced.taskmanager.entity.TodoStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TodoResponseDTO
{
    private Long id;
    private String title;
    private TodoStatus status;

    //Getter methods
    public Long getId()
    {
        return id;
    }
    public String getTitle()
    {
        return title;
    }
    public TodoStatus getStatus()
    {
        return status;
    }

    //Setter methods
    public void setId(Long id)
    {
        this.id = id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setStatus(TodoStatus status)
    {
        this.status = status;
    }
}