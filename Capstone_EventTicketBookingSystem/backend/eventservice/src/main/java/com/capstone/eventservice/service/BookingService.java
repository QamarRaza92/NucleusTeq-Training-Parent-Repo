package com.capstone.eventservice.service;

import com.capstone.eventservice.dto.BookingRequestDTO;
import com.capstone.eventservice.dto.BookingResponseDTO;
import com.capstone.eventservice.dto.UserResponseDTO;
import com.capstone.eventservice.entity.Booking;
import com.capstone.eventservice.entity.Event;
import com.capstone.eventservice.client.UserClient;
import com.capstone.eventservice.exception.BookingNotFoundException;
import com.capstone.eventservice.exception.EventNotFoundException;
import com.capstone.eventservice.exception.UnauthorizedException;
import com.capstone.eventservice.repository.BookingRepository;
import com.capstone.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service 
public class BookingService
{
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserClient userClient;

    @Autowired 
    private EventRepository eventRepository;
                         
    public BookingResponseDTO getBookingById(Long bookingId,Long customerId)
    {
        Booking booking = bookingRepository.findById(bookingId)
                          .orElseThrow(()->new BookingNotFoundException(bookingId));
        if(booking.getAudienceId().equals(customerId))
        {
            return convertToDTO(booking);
        }
        else 
        {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }
    }

    public List<BookingResponseDTO> getMyBookingsByOrganizerId(Long organizerId)
    {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        if (events.isEmpty())
        {
            return List.of();
        }
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        List<BookingResponseDTO> response = bookingRepository.findByEventIdIn(eventIds).stream()
                                            .map(this::convertToDTO).toList();
        return response;    
    }

    public List<BookingResponseDTO> getMyBookingsByCustomerId(Long customerId)
    {
        List<BookingResponseDTO> response = bookingRepository.findByAudienceId(customerId).stream()
                                                             .map(this::convertToDTO).toList();
        return response;
    }


    @Transactional
    public void cancelBooking(Long bookingId,Long customerId)
    {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                                                                ()-> new BookingNotFoundException(bookingId));
        if(!booking.getAudienceId().equals(customerId))
        {
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED)
        {
            throw new RuntimeException("Booking already cancelled");
        }
        
        Event event = eventRepository.findById(booking.getEventId()).orElseThrow(
                                                        ()-> new EventNotFoundException(booking.getEventId()));
        if(event.getStartTime().isBefore(LocalDateTime.now().plusHours(3)))
        {
            throw new RuntimeException("Cannot cancel booking within 3 hours of event start");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        event.setBookedSeats(Math.max(0,event.getBookedSeats() - booking.getNoOfTickets()));
        eventRepository.save(event);
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request,Long customerId)
    {
        Event event = eventRepository.findById(request.getEventId())
                        .orElseThrow(()-> new EventNotFoundException(request.getEventId()));

        if(event.getEventDate().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("Event already ended");
        }
        int availableSeats = event.getTotalSeats() - event.getBookedSeats();

        if(request.getNoOfTickets()>availableSeats)
        {
            throw new RuntimeException("Not enough seats available");
        }
        event.setBookedSeats(event.getBookedSeats()+request.getNoOfTickets());
        eventRepository.save(event);

        Booking booking = new Booking(customerId,request.getEventId(),
                        request.getNoOfTickets(),request.getTotalAmount());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteAllBookingsByEventId(Long eventId,Long organizerId)
    {
        Event event = eventRepository.findById(eventId)
                      .orElseThrow(()-> new EventNotFoundException(eventId));
        if(!event.getOrganizerId().equals(organizerId))
        {
            throw new UnauthorizedException("You are not authorized to cancel this event");
        }
        event.setBookedSeats(0);
        eventRepository.save(event);
        bookingRepository.deleteByEventId(eventId);
    }

    public BookingResponseDTO convertToDTO(Booking booking)
    {
        System.out.println("Calling user-service for: " + booking.getAudienceId());
        UserResponseDTO user = userClient.getUserById(booking.getAudienceId());
        System.out.println("User received: " + user);
        return (new BookingResponseDTO(booking.getId(),booking.getEventId(),booking.getNoOfTickets(),
                                        booking.getTotalAmount(),booking.getStatus().toString(),user.getName(),user.getEmail()));
    }
}