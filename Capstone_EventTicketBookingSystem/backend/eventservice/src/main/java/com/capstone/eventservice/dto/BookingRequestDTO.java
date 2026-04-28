package com.capstone.eventservice.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class BookingRequestDTO
{
    private Long eventId;
    
    @Min(1)
    private Integer noOfTickets;
    
    @Positive(message="amount must be positive")
    private Double totalAmount;
    
    private String status;
    
    private LocalDateTime bookingTime;

    //Constructor
    public BookingRequestDTO(){this.bookingTime = LocalDateTime.now();}

    //setter
    public void setEventId(Long eventId){this.eventId = eventId;}
    public void setNoOfTickets(Integer noOfTickets){this.noOfTickets = noOfTickets;}
    public void setTotalAmount(Double totalAmount){this.totalAmount = totalAmount;}
    public void setStatus(String status){this.status = status;}

    //getter 
    public Long getEventId(){return eventId;}
    public Integer getNoOfTickets(){return noOfTickets;}
    public Double getTotalAmount(){return totalAmount;}
    public String getStatus(){return status;}
    public LocalDateTime getBookingTime(){return bookingTime;}
}