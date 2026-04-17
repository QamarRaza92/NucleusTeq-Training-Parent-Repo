package com.springadvanced.taskmanager.dto;
import com.springadvanced.taskmanager.entity.TodoStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
public class TodoResponseDTO
{
    private Long id;
    private String title;
    
    @Enumerated(EnumType.STRING)
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