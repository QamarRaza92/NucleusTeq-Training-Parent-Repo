package com.capstone.eventservice.service;

import com.capstone.eventservice.client.UserClient;
import com.capstone.eventservice.dto.BookingRequestDTO;
import com.capstone.eventservice.dto.UserResponseDTO;
import com.capstone.eventservice.entity.Booking;
import com.capstone.eventservice.entity.Event;
import com.capstone.eventservice.repository.BookingRepository;
import com.capstone.eventservice.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private BookingService bookingService;

    private Event event;
    private Booking booking;
    private BookingRequestDTO request;
    private UserResponseDTO user;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setTotalSeats(100);
        event.setBookedSeats(30);
        event.setStartTime(LocalDateTime.now().plusDays(5));
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setStatus(Event.EventStatus.ACTIVE);

        booking = new Booking();
        booking.setId(1L);
        booking.setAudienceId(4L);
        booking.setEventId(1L);
        booking.setNoOfTickets(2);
        booking.setTotalAmount(1000.0);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        request = new BookingRequestDTO();
        request.setEventId(1L);
        request.setNoOfTickets(2);
        request.setTotalAmount(1000.0);

        user = new UserResponseDTO();
        user.setName("Test User");
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateBooking_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = bookingService.createBooking(request, 4L);
    
        assertNotNull(response);
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ExceedingCapacity_ThrowsException() {
        request.setNoOfTickets(80);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(request, 4L));
    }

    @Test
    void testCreateBooking_PastEvent_ThrowsException() {
        event.setStartTime(LocalDateTime.now().minusDays(1));
        event.setEventDate(LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(request, 4L));
    }

    @Test
    void testCancelBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertDoesNotThrow(() -> bookingService.cancelBooking(1L, 4L));

        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCancelBooking_Within3Hours_ThrowsException() {
        event.setStartTime(LocalDateTime.now().plusHours(2));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(1L, 4L));
    }

    @Test
    void testCancelBooking_Unauthorized_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(1L, 999L));
    }

    @Test
    void testCancelBooking_AlreadyCancelled_ThrowsException() {
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(RuntimeException.class, () -> bookingService.cancelBooking(1L, 4L));
    }

    @Test
    void testGetMyBookingsByCustomerId_Success() {
        when(bookingRepository.findByAudienceId(4L)).thenReturn(Arrays.asList(booking));
        var response = bookingService.getMyBookingsByCustomerId(4L);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testGetMyBookingsByOrganizerId_Success() {
        when(eventRepository.findByOrganizerId(1L)).thenReturn(Arrays.asList(event));
        when(bookingRepository.findByEventIdIn(any())).thenReturn(Arrays.asList(booking));

        var response = bookingService.getMyBookingsByOrganizerId(1L);
        assertNotNull(response);
    }

    @Test
    void testConvertToDTO_WithFeignSuccess() {
        when(userClient.getUserById(4L)).thenReturn(user);
        var dto = bookingService.convertToDTO(booking);
        assertEquals("Test User", dto.getCustomerName());
    }

    @Test
    void testConvertToDTO_FeignFailure_ReturnsDefault() {
        when(userClient.getUserById(4L)).thenThrow(new RuntimeException("Network error"));
        var dto = bookingService.convertToDTO(booking);
        assertEquals("Unknown User", dto.getCustomerName());
    }
}