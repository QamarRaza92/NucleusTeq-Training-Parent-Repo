package com.capstone.eventservice.controller;

import com.capstone.eventservice.dto.BookingRequestDTO;
import com.capstone.eventservice.dto.BookingResponseDTO;
import com.capstone.eventservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/bookings")
public class BookingController
{
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId,HttpServletRequest request)
    {
        Long customerId = (Long) request.getAttribute("userId");
        if (customerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        BookingResponseDTO response = bookingService.getBookingById(bookingId,customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings-organizer")
    public ResponseEntity<?> getMyBookingsByOrganizer(HttpServletRequest request)
    {
        Long organizerId = (Long) request.getAttribute("userId");
        if (organizerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(bookingService.getMyBookingsByOrganizerId(organizerId));
    }

    @GetMapping("/my-bookings-customer")
    public ResponseEntity<?> getMyBookingsByCustomerId(HttpServletRequest request)
    {
        Long customerId = (Long) request.getAttribute("userId");
        if (customerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        return ResponseEntity.ok(bookingService.getMyBookingsByCustomerId(customerId));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequestDTO request,
                                            HttpServletRequest httpRequest)
    {
        Long customerId = (Long) httpRequest.getAttribute("userId");
        if (customerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        BookingResponseDTO response = bookingService.createBooking(request,customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/customer/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId,
                                            HttpServletRequest httpRequest)
    {
        Long customerId = (Long) httpRequest.getAttribute("userId");
        if (customerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        bookingService.cancelBooking(bookingId,customerId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @DeleteMapping("/organizer/{eventId}")
    public ResponseEntity<?> deleteAllBookingsByEventId(@PathVariable Long eventId,
                                                        HttpServletRequest httpRequest)
    {
        Long organizerId = (Long) httpRequest.getAttribute("userId");
        if (organizerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        bookingService.deleteAllBookingsByEventId(eventId,organizerId);
        return ResponseEntity.ok("All bookings for event id: " + eventId + " deleted by organizer " + organizerId);
    }
}