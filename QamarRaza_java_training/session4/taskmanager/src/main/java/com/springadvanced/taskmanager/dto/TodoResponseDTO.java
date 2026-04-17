package com.springadvanced.taskmanager.dto;

public class TodoResponseDTO
{
    private Long id;
    private String title;
    private String status;

    //Getter methods
    public Long getId()
    {
        return id;
    }
    public String getTitle()
    {
        return title;
    }
    public String getStatus()
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
    public void setStatus(String status)
    {
        this.status = status;
    }
}