package com.capstone.eventservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "audience_id", nullable = false)
    private Long audienceId;
    
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @Column(name = "no_of_tickets", nullable = false)
    @Min(1)
    private Integer noOfTickets;
    
    @Column(name = "total_amount", nullable = false)
    @Positive(message="amount must be positive")
    private Double totalAmount;
    
    public enum BookingStatus {
        CONFIRMED,
        CANCELLED,
        CANCELLED_BY_ORGANIZER
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name="booking_status")
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "bookingTime")
    private LocalDateTime bookingTime;
    
    @PrePersist
    protected void onCreate() {
        bookingTime = LocalDateTime.now();
    }

    public Booking(){}

    public Booking(Long audienceId,Long eventId,Integer noOfTickets,Double totalAmount)
    {
        this.audienceId = audienceId;
        this.eventId = eventId;
        this.noOfTickets = noOfTickets;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAudienceId() { return audienceId; }
    public void setAudienceId(Long audienceId) { this.audienceId = audienceId; }
    
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    
    public Integer getNoOfTickets() { return noOfTickets; }
    public void setNoOfTickets(Integer noOfTickets) { this.noOfTickets = noOfTickets; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}