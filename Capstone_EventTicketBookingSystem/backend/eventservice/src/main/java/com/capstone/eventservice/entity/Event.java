package com.capstone.eventservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity 
@Table(name="events")
public class Event
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable=false)
    @Pattern(regexp = "^[A-Za-z0-9 .-]{2,}$", message = "Invalid event name")
    private String name;

    @Column(name = "description", nullable=false)
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "event_date", nullable=false)
    @FutureOrPresent(message = "Event date must be in future")
    private LocalDateTime eventDate;

    @Column(name = "start_time", nullable=false)
    @FutureOrPresent(message = "Start time must be in future")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable=false)
    @FutureOrPresent(message = "End time must be in future")
    private LocalDateTime endTime;

    @Column(name = "venue", nullable=false)
    private String venue;

    @Column(name = "total_seats", nullable=false)

    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;

    @Column(name = "booked_seats")
    @Min(0)
    private Integer bookedSeats = 0;

    @Column(name = "price", nullable=false)
    @Positive(message = "Price must be positive")
    private Double price;

    @Column(name="category")
    private String category;

    @Column(name = "organizer_id", nullable=false)
    private Long organizerId;

    @Column(name = "created_at", nullable=false)
    private LocalDateTime createdAt;

    public enum EventStatus
    {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private EventStatus status;

    @PrePersist
    protected void onCreate()
    {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public Integer getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(Integer bookedSeats) { this.bookedSeats = bookedSeats; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
}