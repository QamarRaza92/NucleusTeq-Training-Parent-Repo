package com.capstone.eventservice.dto;

import java.time.LocalDateTime;
public class EventResponseDTO
{
    private Long id;
    private String name;
    private String venue;
    private LocalDateTime eventDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalSeats;
    private Integer bookedSeats;  
    private Double price;
    private String status;
    private Long organizerId;

    public void setId(Long id){this.id=id;}
    public void setName(String name){this.name=name;}
    public void setVenue(String venue){this.venue=venue;}
    public void setEventDate(LocalDateTime eventDate){this.eventDate=eventDate;}
    public void setStartTime(LocalDateTime startTime){this.startTime=startTime;}
    public void setEndTime(LocalDateTime endTime){this.endTime=endTime;}
    public void setTotalSeats(Integer totalSeats){this.totalSeats=totalSeats;}
    public void setBookedSeats(Integer bookedSeats){this.bookedSeats=bookedSeats;}
    public void setPrice(Double price){this.price=price;}
    public void setStatus(String status){this.status=status;}
    public void setOrganizerId(Long organizerId){this.organizerId=organizerId;}

    public Long getId(){return id;}
    public String getName(){return name;}
    public String getVenue(){return venue;}
    public LocalDateTime getEventDate(){return eventDate;}
    public LocalDateTime getStartTime(){return startTime;}
    public LocalDateTime getEndTime(){return endTime;}
    public Integer getTotalSeats(){return totalSeats;}
    public Integer getBookedSeats(){return bookedSeats;}
    public Double getPrice(){return price;}
    public String getStatus(){return status;}
    public Long getOrganizerId(){return organizerId;}
}