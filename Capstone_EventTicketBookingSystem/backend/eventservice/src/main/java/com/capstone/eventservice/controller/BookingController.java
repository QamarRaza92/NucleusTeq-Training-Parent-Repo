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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/bookings")
public class BookingController
{
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    //Get all bookings for the events organized by an organizer
    @GetMapping("/my-bookings-organizer")
    public ResponseEntity<?> getMyBookingsByOrganizer(HttpServletRequest request)
    {
        Long organizerId = (Long) request.getAttribute("userId");
        if (organizerId == null)
        {
            log.warn("Unauthorized: No user ID found in request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Get Organizer Bookings - Organizer: {}", organizerId);
        return ResponseEntity.ok(bookingService.getMyBookingsByOrganizerId(organizerId));
    }

    //Get all bookings done by a specific customer
    @GetMapping("/my-bookings-customer")
    public ResponseEntity<?> getMyBookingsByCustomerId(HttpServletRequest request)
    {
        Long customerId = (Long) request.getAttribute("userId");
        if (customerId == null)
        {
            log.warn("Unauthorized: No user ID found in request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Get Customer Bookings - Customer: {}", customerId);
        return ResponseEntity.ok(bookingService.getMyBookingsByCustomerId(customerId));
    }

    //Create bookings, it automatically gets triggered when a customer books a ticket.
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequestDTO request,
                                            HttpServletRequest httpRequest)
    {
        Long customerId = (Long) httpRequest.getAttribute("userId");
        if (customerId == null)
        {
            log.warn("Unauthorized: No user ID found in request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Create Booking - Customer: {}, Event: {}, Tickets: {}", 
                 customerId, request.getEventId(), request.getNoOfTickets());
        BookingResponseDTO response = bookingService.createBooking(request,customerId);
        log.info("API Success: Booking created - ID: {}, Customer: {}, Event: {}", 
                 response.getId(), customerId, request.getEventId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Cancel an event by customer
    @PutMapping("/customer/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId,
                                            HttpServletRequest httpRequest)
    {
        Long customerId = (Long) httpRequest.getAttribute("userId");
        if (customerId == null)
        {
            log.warn("Unauthorized: No user ID found in request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Cancel Booking - BookingId: {}, Customer: {}", bookingId, customerId);
        bookingService.cancelBooking(bookingId,customerId);
        log.info("API Success: Booking cancelled - ID: {}, Customer: {}", bookingId, customerId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}