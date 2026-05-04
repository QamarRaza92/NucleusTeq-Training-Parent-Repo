package com.capstone.eventservice.service;

import com.capstone.eventservice.dto.EventRequestDTO;
import com.capstone.eventservice.dto.EventResponseDTO;
import com.capstone.eventservice.entity.Event;
import com.capstone.eventservice.exception.EventNotFoundException;
import com.capstone.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import com.capstone.eventservice.entity.Booking;
import com.capstone.eventservice.repository.BookingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service 
public class EventService
{
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    //Create event requires an event request along with organizer id.
    public EventResponseDTO createEvent(EventRequestDTO request, Long organizerId)
    {
        log.info("Create Event Request By Organizer:{}",organizerId);
        LocalDateTime now = LocalDateTime.now();

        if(request.getStartTime().isBefore(LocalDateTime.now()))
        {
            log.warn("Invalid Inputs: Event must start in future");
            throw new RuntimeException("Event time must be in future");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            log.warn("Invalid Input: Event end time must be after start time");
            throw new IllegalArgumentException("End time must be after start time");
        }
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getStartTime());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setVenue(request.getVenue());
        event.setTotalSeats(request.getTotalSeats());
        event.setBookedSeats(0);
        event.setPrice(request.getPrice());
        event.setCategory(request.getCategory());
        event.setOrganizerId(organizerId);
        event.setStatus(Event.EventStatus.ACTIVE);

        Event savedEntity = eventRepository.save(event);
        log.info("Event Scheduled: EventId:{}, OrganizerId:{}",savedEntity.getId(), event.getOrganizerId());
        return convertToDTO(savedEntity);
    }

    //To return response, we are converting Entities into DTO
    public EventResponseDTO convertToDTO(Event entity)
    {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVenue(entity.getVenue());
        dto.setEventDate(entity.getEventDate());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setTotalSeats(entity.getTotalSeats());
        dto.setDescription(entity.getDescription());
        dto.setBookedSeats(entity.getBookedSeats());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus().toString());
        dto.setOrganizerId(entity.getOrganizerId()); 
        return dto;
    }

    //To update events, we pass "update request object" and eventId along with organizerId
    public EventResponseDTO updateEvent(EventRequestDTO request,Long eventId,Long organizerId)
    {
        log.info("Updation Request Received: EventId:{}, OrganizerId:{}",eventId,organizerId);
        Event existingEvent = eventRepository.findById(eventId)
                                .orElseThrow(()->
                                                {
                                                    log.error("Updation Failed: Event ID:{} not found!",eventId);
                                                    return new EventNotFoundException(eventId);                                              
                                                }
                                            );

        if(!existingEvent.getOrganizerId().equals(organizerId))
        {
            log.warn("Security Violation: Organizer:{} tried to update Event:{}",organizerId,eventId);
            throw new RuntimeException("You are not authorized to update this event");
        }

        if(existingEvent.getStatus()!=Event.EventStatus.ACTIVE)
        {
            log.warn("Validation Failed: Cannot update a 'COMPLETED' or 'CANCELLED' event");
            throw new RuntimeException("Updation is allowed for 'UPCOMING' events only");
        }

        if(request.getEndTime().isBefore(request.getStartTime()))
        {
            log.warn("Invalid Inputs: End time must be after start time");
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (existingEvent.getStartTime().isBefore(LocalDateTime.now().plusHours(4))) 
        {
            log.warn("Validation Failed: Cannot update event within 4 hours of start time");
            throw new RuntimeException("Cannot update event within 4 hours of start time");
        }

        if(request.getTotalSeats()<existingEvent.getBookedSeats())
        {
            log.warn("Validation failed: {} seats have already been booked. Cannot reduce seating limit to {}",existingEvent.getBookedSeats(),request.getTotalSeats());
            throw new RuntimeException(
                "Cannot reduce total seats below booked seats. Requested: "
                + request.getTotalSeats() + ", Already booked: "
                + existingEvent.getBookedSeats()
            );
        }

        existingEvent.setName(request.getName());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setEventDate(request.getEventDate());
        existingEvent.setStartTime(request.getStartTime());
        existingEvent.setEndTime(request.getEndTime());
        existingEvent.setVenue(request.getVenue());
        existingEvent.setTotalSeats(request.getTotalSeats());
        existingEvent.setPrice(request.getPrice());
        existingEvent.setCategory(request.getCategory());
        
        Event updatedEvent = eventRepository.save(existingEvent);
        log.info("Event Updation Successful: Organizer:{}, Event:{}",organizerId,eventId);
        return convertToDTO(updatedEvent);
    }

    //To fetch event by id
    public EventResponseDTO getEventById(Long eventId)
    {
        log.info("Fetching details for event:{}",eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new EventNotFoundException(eventId));
        return convertToDTO(event);
    }

    //For customer view, to fetch all events by its status (ACTIVE,COMPLETED). "CANCELLED" events are shown to customer due to privacy or organizer.
    public List<EventResponseDTO> getEventsByStatus(String status)
    {
        log.info("Fetching details for all '{}' events for customers",status);
        return eventRepository.findByStatus(Event.EventStatus.valueOf(status.toUpperCase())).stream().map(this::convertToDTO).toList();
    }

    //All events of an organizer by its ID
    public List<EventResponseDTO> getAnOrganizerAllEventsByOrganizerId(Long organizerId)
    {
        log.info("Fetching all events for Organizer:{}",organizerId);
        return eventRepository.findByOrganizerId(organizerId)
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    //Get event an organizer all events by its status (To show on upcoming,completed and cancelled events)
    public List<EventResponseDTO> getEventsByOrganizerAndStatus(Long organizerId, String status)
    {
        log.info("Fetching all '{}' events of Organizer:{}",status,organizerId);
        Event.EventStatus eventStatus = Event.EventStatus.valueOf(status.toUpperCase());
        return eventRepository.findByOrganizerIdAndStatus(organizerId, eventStatus)
                .stream().sorted(Comparator.comparing(Event::getEventDate)).map(this::convertToDTO).toList();
    }

    //Cancel an event organizer by organizer. Cancelling event also updates booking status as "CANCELLED_BY_ORGANIZER".
    @Transactional
    public void cancelEvent(Long eventId,Long organizerId)
    {
        log.info("Cancellation Request: Received request for cancelling Event:{} by Organizer:{}",eventId,organizerId);
        Event event = eventRepository.findById(eventId).orElseThrow(()->
                                                                    {
                                                                        log.error("Cannot find Event:{}",eventId);
                                                                        return new EventNotFoundException(eventId);
                                                                    }
                                                                    );
        if (!event.getOrganizerId().equals(organizerId))    
        {
            log.warn("Unauthorized Cancellation: Organizer:{} tried to cancel Event:{}",organizerId,eventId);
            throw new RuntimeException("Not authorized");
        }
        
        if (event.getStatus() == Event.EventStatus.COMPLETED)
        {
            log.warn("Validation Failed: Cannot cancel a 'COMPLETED' event");
            throw new RuntimeException("Cannot cancel a completed event");
        }

        if (LocalDateTime.now().isAfter(event.getStartTime().minusHours(3))) 
        {
            log.warn("Validation Failed: Cannot cancel event within 3 hours of start time");
            throw new RuntimeException("Cannot cancel event within 3 hours of start time");
        }

        List<Booking> bookings = bookingRepository.findByEventId(eventId);
        for(Booking booking : bookings)
        {
            booking.setStatus(Booking.BookingStatus.CANCELLED_BY_ORGANIZER);
        }
        bookingRepository.saveAll(bookings);
        event.setStatus(Event.EventStatus.CANCELLED);
        eventRepository.save(event);
        log.info("Cancellation Successful: Cancelled event:{} by Organizer:{}",eventId,organizerId);
    }

    //To update event status in every hour based on its date.
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateEventStatus()
    {
        //Changing status from ACTIVE to COMPLETED for the events which have already been done.
        log.info("Cron Job: Starting scheduled event status update...");
        List<Event> pastEvents = eventRepository.findByEventDateBeforeAndStatus(LocalDateTime.now(),Event.EventStatus.ACTIVE);
        if (!pastEvents.isEmpty())
        {
            for(Event event: pastEvents)
            {
                event.setStatus(Event.EventStatus.COMPLETED);
            }
            eventRepository.saveAll(pastEvents);
            log.info("Cron Job: Successfully marked {} events as COMPLETED", pastEvents.size());
        }
        else
        {
            log.info("Cron Job: No active past events found to update.");
        }
    }
}