package com.springadvanced.taskmanager.dto;

public class TodoRequestDTO
{
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