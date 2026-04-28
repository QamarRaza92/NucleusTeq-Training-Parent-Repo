package com.capstone.eventservice.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public class EventRequestDTO
{
    @Pattern(regexp = "^[A-Za-z0-9 .-]{2,}$", message = "Invalid event name")
    private String name;

    private String venue;

    @NotBlank(message = "Description is required")
    private String description;

    @FutureOrPresent(message = "Event date must be in future")
    private LocalDateTime eventDate;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @FutureOrPresent(message = "End time must be in future")
    private LocalDateTime endTime;

    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;

    @Positive(message = "Event price must be positive")
    private Double price;

    private String category;

    private LocalDateTime createdAt;

    public void setName(String name){this.name=name;}
    public void setVenue(String venue){this.venue=venue;}
    public void setDescription(String description){this.description=description;}
    public void setEventDate(LocalDateTime eventDate){this.eventDate=eventDate;}
    public void setStartTime(LocalDateTime startTime){this.startTime=startTime;}
    public void setEndTime(LocalDateTime endTime){this.endTime=endTime;}
    public void setTotalSeats(Integer totalSeats){this.totalSeats=totalSeats;}
    public void setPrice(Double price){this.price=price;}
    public void setCategory(String category){this.category=category;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}

    public String getName(){return name;}
    public String getVenue(){return venue;}
    public String getDescription(){return description;}
    public LocalDateTime getEventDate(){return eventDate;}
    public LocalDateTime getStartTime(){return startTime;}
    public LocalDateTime getEndTime(){return endTime;}
    public Integer getTotalSeats(){return totalSeats;}
    public Double getPrice(){return price;}
    public String getCategory(){return category;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}