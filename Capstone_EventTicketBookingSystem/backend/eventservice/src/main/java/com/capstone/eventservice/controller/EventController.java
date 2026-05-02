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
@RestController
@RequestMapping("/api/events")
public class EventController
{
    @Autowired
    private EventService eventService;

    //For creating an event through organizer.
    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequestDTO request, 
                                        HttpServletRequest httpRequest)
    {
        Long organizerId = (Long) httpRequest.getAttribute("userId");
        if (organizerId == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        EventResponseDTO response = eventService.createEvent(request,organizerId); 
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        EventResponseDTO response = eventService.updateEvent(request,eventId,organizerId);
        return ResponseEntity.ok(response);
    }

    //Get an event details.
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId)
    {
        EventResponseDTO response = eventService.getEventById(eventId);
        return ResponseEntity.ok(response);
    }

    //For customer view, get all ACTIVE or COMPLETED events, Based on query param (not showing cancelled event to customer, bcoz event was cancelled by organzer should not be seen to customer)
    @GetMapping("/customer-events")
    public ResponseEntity<?> getEventsByStatus(@RequestParam String status)
    {
        if(!status.equals("ACTIVE") && !status.equals("COMPLETED"))
        {
            return ResponseEntity.badRequest().body("Invalid status. Use: ACTIVE, COMPLETED, CANCELLED");
        }
        List<EventResponseDTO> response = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(response);
    }

    //Get all available events of organizer to show on its dashboard (be it ACTIVE,COMPLETED or CANCELLED)
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<?> getAnOrganizerAllEventsByOrganizerId(@PathVariable Long organizerId)
    {
        List<EventResponseDTO> response = eventService.getAnOrganizerAllEventsByOrganizerId(organizerId);
        return ResponseEntity.ok(response);
    }

    //Get organizer events by status(ACTIVE,COMPLETED,CANCELLED) to show on upcoming,completed and cancelled event pages separately.
    @GetMapping("/organizer/events")
    public ResponseEntity<?> getEventsByOrganizerAndStatus(@RequestParam String status, HttpServletRequest request)
    {
        Long organizerId = (Long) request.getAttribute("userId");
        if (organizerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!status.equals("COMPLETED") && !status.equals("ACTIVE") && !status.equals("CANCELLED")) {
            return ResponseEntity.badRequest().body("Invalid status. Use: ACTIVE, COMPLETED, CANCELLED");
        }
        List<EventResponseDTO> response = eventService.getEventsByOrganizerAndStatus(organizerId, status);
        return ResponseEntity.ok(response);
    }

    //Cancel an event thorugh organizer
    @PutMapping("/cancel/{eventId}")
    public ResponseEntity<?> cancelEvent(@PathVariable Long eventId,HttpServletRequest httpRequest)
    {
        Long organizerId = (Long) httpRequest.getAttribute("userId");
        eventService.cancelEvent(eventId,organizerId);
        return ResponseEntity.ok("Event cancelled successfully");
    }
}

