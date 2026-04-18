package com.springadvanced.taskmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity 
@Table(name="todos")
public class TodoEntity
{
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TodoStatus status;
    private LocalDateTime createdAt;

    //All setter methods
    public void setId(Long id)
    {
        this.id = id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public void setStatus(TodoStatus status)
    {
        this.status = status;
    }
    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    //All getter methods
    public Long getId()
    {
        return id;
    }
    public String getTitle()
    {
        return title;
    }
    public String getDescription()
    {
        return description;
    }
    public TodoStatus getStatus()
    {
        return status;
    }
    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }
}