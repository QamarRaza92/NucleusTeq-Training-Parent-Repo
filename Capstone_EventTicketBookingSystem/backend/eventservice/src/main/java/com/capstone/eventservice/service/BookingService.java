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

    //Get all bookings for the events organizer by a specific organizer
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

    //Get all bookings done by a customer
    public List<BookingResponseDTO> getMyBookingsByCustomerId(Long customerId)
    {
        List<BookingResponseDTO> response = bookingRepository.findByAudienceId(customerId).stream()
                                                             .map(this::convertToDTO).toList();
        return response;
    }

    //To cancel booking by a customer. Cancellation also affects organizer revenue. Can't cancel bookings within 3 hours of event start.
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

    //To create booking by a customer. Booking cant be done after event has already been COMPLETED
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request,Long customerId)
    {
        Event event = eventRepository.findById(request.getEventId())
                        .orElseThrow(()-> new EventNotFoundException(request.getEventId()));

        if(event.getEventDate().isBefore(LocalDateTime.now()))
        {
            throw new RuntimeException("Event already ended");
        }

        if(event.getBookedSeats() > event.getTotalSeats())
        {
            throw new RuntimeException("Cannot Book more seats then available!");
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

    //For returning DTO instead of complete Entity(It uses feign client facility)
    public BookingResponseDTO convertToDTO(Booking booking)
    {
        UserResponseDTO user = userClient.getUserById(booking.getAudienceId());
        return (new BookingResponseDTO(booking.getId(),booking.getEventId(),booking.getNoOfTickets(),
                                        booking.getTotalAmount(),booking.getStatus().toString(),user.getName(),user.getEmail()));
    }
}