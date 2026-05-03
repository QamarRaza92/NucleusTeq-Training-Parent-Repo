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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service 
public class BookingService
{
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserClient userClient;

    @Autowired 
    private EventRepository eventRepository;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    //Get all bookings for the events organizer by a specific organizer
    public List<BookingResponseDTO> getMyBookingsByOrganizerId(Long organizerId)
    {
        log.info("Fetching all event bookings for Organizer ID: {}", organizerId);
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        if (events.isEmpty())
        {
            log.info("No events found for Organizer ID: {}", organizerId);
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
        log.info("Fetching booking history for Customer ID: {}", customerId);
        List<BookingResponseDTO> response = bookingRepository.findByAudienceId(customerId).stream()
                                                             .map(this::convertToDTO).toList();
        return response;
    }

    //To cancel booking by a customer. Cancellation also affects organizer revenue. Can't cancel bookings within 3 hours of event start.
    @Transactional
    public void cancelBooking(Long bookingId,Long customerId)
    {
        log.info("Cancellation request for Booking:{} by customer:{}",bookingId,customerId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> 
                                                                            {
                                                                             log.error("Cancellation failed: Booking ID {} not found", bookingId);
                                                                             return new BookingNotFoundException(bookingId);
                                                                            }
                                                                            );
        if(!booking.getAudienceId().equals(customerId))
        {
            log.warn("Security Alert: Unauthorized cancellation attempt on Booking {} by User {}", bookingId, customerId);
            throw new UnauthorizedException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED)
        {
            log.warn("Redundant Action: Booking {} is already cancelled", bookingId);
            throw new RuntimeException("Booking already cancelled");
        }
        
        Event event = eventRepository.findById(booking.getEventId()).orElseThrow(
                                                        ()-> new EventNotFoundException(booking.getEventId()));

        if (event.getStatus() == Event.EventStatus.COMPLETED)
        {
            log.warn("Validation failed: Cannot cancel booking for completed Event {}", event.getId());
            throw new RuntimeException("Cannot cancel a completed event");
        }

        if(event.getStartTime().isBefore(LocalDateTime.now().plusHours(3)))
        {
            log.warn("Policy Violation: Customer {} tried to cancel Booking {} within 3-hour window of Event {}",customerId, bookingId, event.getId());
            throw new RuntimeException("Cannot cancel booking within 3 hours of event start");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        event.setBookedSeats(Math.max(0,event.getBookedSeats() - booking.getNoOfTickets()));
        eventRepository.save(event);
        log.info("Successfully cancelled Booking {}. Released {} seats for Event {}",bookingId, booking.getNoOfTickets(), event.getId());
    }

    //To create booking by a customer. Booking cant be done after event has already been COMPLETED
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request,Long customerId)
    {
        log.info("New Booking Request -> Event: {}, Customer: {}, Tickets: {}",request.getEventId(), customerId, request.getNoOfTickets());
        Event event = eventRepository.findById(request.getEventId())
                        .orElseThrow(() -> {
                            log.error("Booking failed: Event ID {} not found", request.getEventId());
                            return new EventNotFoundException(request.getEventId());
                        });

        if(event.getEventDate().isBefore(LocalDateTime.now()))
        {
            log.warn("Booking failed: Attempted to book past Event {}", event.getId());
            throw new RuntimeException("Event already ended");
        }

        if(event.getBookedSeats() > event.getTotalSeats())
        {
            log.warn("Booking failed: Cannot book more than available seats for event:{}",event.getId());
            throw new RuntimeException("Cannot Book more seats then available!");
        }
        int availableSeats = event.getTotalSeats() - event.getBookedSeats();

        if(request.getNoOfTickets()>availableSeats)
        {
            log.warn("Booking failed: Insufficient seats for Event {}. Requested: {}, Available: {}",event.getId(), request.getNoOfTickets(), availableSeats);
            throw new RuntimeException("Not enough seats available");
        }
        event.setBookedSeats(event.getBookedSeats()+request.getNoOfTickets());
        eventRepository.save(event);

        Booking booking = new Booking(customerId,request.getEventId(),
                        request.getNoOfTickets(),request.getTotalAmount());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking confirmed! ID: {}, Event: {}, Customer: {}", saved.getId(), event.getId(), customerId);
        return convertToDTO(saved);
    }

    //For returning DTO instead of complete Entity(It uses feign client facility)
    public BookingResponseDTO convertToDTO(Booking booking)
    {
        try 
        {
            UserResponseDTO user = userClient.getUserById(booking.getAudienceId());
            return (new BookingResponseDTO(booking.getId(),booking.getEventId(),booking.getNoOfTickets(),
                                            booking.getTotalAmount(),booking.getStatus().toString(),user.getName(),user.getEmail()));            
        }
        catch(Exception e)
        {
            log.error("Network Error: Could not fetch User details for Audience ID {} via UserClient. Reason: {}", 
                      booking.getAudienceId(), e.getMessage());
            return new BookingResponseDTO(
                booking.getId(), 
                booking.getEventId(), 
                booking.getNoOfTickets(),
                booking.getTotalAmount(), 
                booking.getStatus().toString(), 
                "Unknown User", 
                "N/A"
            );
        }
    }
}