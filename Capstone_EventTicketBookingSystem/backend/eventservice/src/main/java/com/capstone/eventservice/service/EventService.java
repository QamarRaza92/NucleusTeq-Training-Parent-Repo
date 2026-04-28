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
import java.util.Comparator;
@Service 
public class EventService
{
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public EventResponseDTO createEvent(EventRequestDTO request, Long organizerId)
    {
        LocalDateTime now = LocalDateTime.now();

        if(request.getStartTime().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("Event time must be in future");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
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
        return convertToDTO(savedEntity);
    }

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
        dto.setBookedSeats(entity.getBookedSeats());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus().toString());
        dto.setOrganizerId(entity.getOrganizerId()); 
        return dto;
    }

    public EventResponseDTO updateEvent(EventRequestDTO request,Long eventId,Long organizerId)
    {
        Event existingEvent = eventRepository.findById(eventId)
                                .orElseThrow(()->new EventNotFoundException(eventId));

        if(!existingEvent.getOrganizerId().equals(organizerId))
        {
            throw new RuntimeException("You are not authorized to update this event");
        }

        if(request.getEndTime().isBefore(request.getStartTime()))
        {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (existingEvent.getStartTime().isBefore(LocalDateTime.now().plusHours(4))) 
        {
            throw new RuntimeException("Cannot update event within 4 hours of start time");
        }

        if(request.getTotalSeats()<existingEvent.getBookedSeats())
        {
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
        return convertToDTO(updatedEvent);
    }

    public List<EventResponseDTO> getAllEvents()
    {
        return eventRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public EventResponseDTO getEventById(Long eventId)
    {
        Event event = eventRepository.findById(eventId).orElseThrow(()-> new EventNotFoundException(eventId));
        return convertToDTO(event);
    }

    public List<EventResponseDTO> getEventsByStatus(String status)
    {
        return eventRepository.findByStatus(Event.EventStatus.valueOf(status.toUpperCase())).stream().map(this::convertToDTO).toList();
    }

    public List<EventResponseDTO> getUpcomingEventsOfOrganizer(Long organizerId)
    {
        return eventRepository.findByOrganizerIdAndStatus(organizerId,Event.EventStatus.ACTIVE).stream().map(this::convertToDTO).toList();
    }

    public List<EventResponseDTO> getCompletedEventsOfOrganizer(Long organizerId)
    {
        return eventRepository.findByOrganizerIdAndStatus(organizerId,Event.EventStatus.COMPLETED).stream().map(this::convertToDTO).toList();
    }

    public List<EventResponseDTO> getCancelledEventsOfOrganizer(Long organizerId)
    {
        return eventRepository.findByOrganizerIdAndStatus(organizerId,Event.EventStatus.CANCELLED).stream().map(this::convertToDTO).toList();
    }

    public List<EventResponseDTO> getAnOrganizerAllEventsByOrganizerId(Long organizerId) {
    return eventRepository.findByOrganizerId(organizerId)
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    public List<EventResponseDTO> getEventsByOrganizerAndStatus(Long organizerId, String status)
    {
        Event.EventStatus eventStatus = Event.EventStatus.valueOf(status.toUpperCase());
        return eventRepository.findByOrganizerIdAndStatus(organizerId, eventStatus)
                .stream().sorted(Comparator.comparing(Event::getEventDate)).map(this::convertToDTO).toList();
    }

    @Transactional
    public void cancelEvent(Long eventId,Long organizerId)
    {
        Event event = eventRepository.findById(eventId).orElseThrow(()->new EventNotFoundException(eventId));
        if (!event.getOrganizerId().equals(organizerId))    
        {
            throw new RuntimeException("Not authorized");
        }

        if (LocalDateTime.now().isAfter(event.getStartTime().minusHours(3))) 
        {
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
    }
}