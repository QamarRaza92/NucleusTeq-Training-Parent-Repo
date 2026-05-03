package com.capstone.eventservice.controller;

import com.capstone.eventservice.dto.EventRequestDTO;
import com.capstone.eventservice.dto.EventResponseDTO;
import com.capstone.eventservice.service.EventService;
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
@RequestMapping("/api/events")
public class EventController
{
    @Autowired
    private EventService eventService;

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    //For creating an event through organizer.
    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequestDTO request, 
                                        HttpServletRequest httpRequest)
    {
        Long organizerId = (Long) httpRequest.getAttribute("userId");
        if (organizerId == null)
        {
            log.warn("Invalid Organizer: Organizer must not be null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Create Event - Organizer: {}, Event: {}", organizerId, request.getName());
        EventResponseDTO response = eventService.createEvent(request,organizerId); 
        log.info("API Success: Event created - ID: {}, Name: {}", response.getId(), response.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //For creating an event through organizer.
    @PutMapping("/update/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId,
                                         @Valid @RequestBody EventRequestDTO request,
                                         HttpServletRequest httpRequest
                                         )
    {
        Long organizerId = (Long) httpRequest.getAttribute("userId");
        if (organizerId == null)
        {
            log.warn("Invalid Organizer: Organizer must not be null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Update Event - EventId: {}, Organizer: {}", eventId, organizerId);
        EventResponseDTO response = eventService.updateEvent(request,eventId,organizerId);
        log.info("API Success: Event updated - ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    //Get an event details.
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId)
    {
        log.info("API Call: Get Event by ID - EventId: {}", eventId);
        EventResponseDTO response = eventService.getEventById(eventId);
        log.info("API Success: Event details fetched - ID: {}, Name: {}", response.getId(), response.getName());
        return ResponseEntity.ok(response);
    }

    //For customer view, get all ACTIVE or COMPLETED events, Based on query param (not showing cancelled event to customer, bcoz event was cancelled by organzer should not be seen to customer)
    @GetMapping("/customer-events")
    public ResponseEntity<?> getEventsByStatus(@RequestParam String status)
    {
        log.info("API Call: Get Customer Events - Status: {}", status);
        if(!status.equals("ACTIVE") && !status.equals("COMPLETED"))
        {
            log.warn("Failed Attempt: Customer can only view 'ACTIVE' and 'COMPLETED' events");
            return ResponseEntity.badRequest().body("Invalid status. Use: ACTIVE, COMPLETED, CANCELLED");
        }
        List<EventResponseDTO> response = eventService.getEventsByStatus(status);
        log.info("API Success: Found {} events with status: {}", response.size(), status);
        return ResponseEntity.ok(response);
    }

    //Get all available events of organizer to show on its dashboard (be it ACTIVE,COMPLETED or CANCELLED)
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<?> getAnOrganizerAllEventsByOrganizerId(@PathVariable Long organizerId)
    {
        log.info("API Call: Get All Events for Organizer - ID: {}", organizerId);
        List<EventResponseDTO> response = eventService.getAnOrganizerAllEventsByOrganizerId(organizerId);
        log.info("API Success: Found {} total events for Organizer: {}", response.size(), organizerId);
        return ResponseEntity.ok(response);
    }

    //Get organizer events by status(ACTIVE,COMPLETED,CANCELLED) to show on upcoming,completed and cancelled event pages separately.
    @GetMapping("/organizer/events")
    public ResponseEntity<?> getEventsByOrganizerAndStatus(@RequestParam String status, HttpServletRequest request)
    {
        Long organizerId = (Long) request.getAttribute("userId");
        if (organizerId == null)
        {
            log.warn("Invalid Organizer: Organizer must not be null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        log.info("API Call: Get Organizer Events - Organizer: {}, Status: {}", organizerId, status);
        if (!status.equals("COMPLETED") && !status.equals("ACTIVE") && !status.equals("CANCELLED")) {
            log.warn("Invalid Status: Event status must be either 'ACTIVE' or 'COMPLETED' or 'CANCELLED'");
            return ResponseEntity.badRequest().body("Invalid status. Use: ACTIVE, COMPLETED, CANCELLED");
        }
        List<EventResponseDTO> response = eventService.getEventsByOrganizerAndStatus(organizerId, status);
        log.info("API Success: Found {} '{}' events for Organizer: {}", response.size(), status, organizerId);
        return ResponseEntity.ok(response);
    }

    //Cancel an event thorugh organizer
    @PutMapping("/cancel/{eventId}")
    public ResponseEntity<?> cancelEvent(@PathVariable Long eventId,HttpServletRequest httpRequest)
    {
        log.info("API Call: Cancel Event - EventId: {}, Organizer: {}", eventId, organizerId);
        Long organizerId = (Long) httpRequest.getAttribute("userId");
        if (organizerId == null)
        {
            log.warn("Invalid Organizer: Organizer must not be null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        eventService.cancelEvent(eventId,organizerId);
        log.info("API Success: Event cancelled - ID: {}", eventId);
        return ResponseEntity.ok("Event cancelled successfully");
    }
}

