package com.springadvanced.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TodoRequestDTO
{
    @NotNull(message="Title cannot be null")
    @Size(min=3, message="Title length must not be smaller than 3 letters")
    private String title;
    private String description;

    //Getter methods
    public String getTitle()
    {
        return title;
    }
    public String getDescription()
    {
        return description;
    }

    //Setter methods
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
}