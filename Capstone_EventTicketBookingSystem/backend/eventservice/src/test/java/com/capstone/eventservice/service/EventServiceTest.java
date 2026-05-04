package com.capstone.eventservice.service;

import com.capstone.eventservice.dto.EventRequestDTO;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventRequestDTO request;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setDescription("Test Description");
        event.setVenue("Test Venue");
        event.setTotalSeats(100);
        event.setBookedSeats(0);
        event.setPrice(BigDecimal.valueOf(500));
        event.setStartTime(LocalDateTime.now().plusDays(5));
        event.setEndTime(LocalDateTime.now().plusDays(5).plusHours(2));
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setStatus(Event.EventStatus.ACTIVE);
        event.setOrganizerId(1L);

        request = new EventRequestDTO();
        request.setName("New Event");
        request.setDescription("New Description");
        request.setVenue("New Venue");
        request.setTotalSeats(200);
        request.setPrice(BigDecimal.valueOf(1000));
        request.setStartTime(LocalDateTime.now().plusDays(10));
        request.setEndTime(LocalDateTime.now().plusDays(10).plusHours(3));
    }

    @Test
    void testCreateEvent_Success() {
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
        var response = eventService.createEvent(request, 1L);
        assertNotNull(response);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEvent_PastDate_ThrowsException() {
        request.setStartTime(LocalDateTime.now().minusDays(1));
        assertThrows(RuntimeException.class, () -> eventService.createEvent(request, 1L));
    }

    @Test
    void testCreateEvent_EndTimeBeforeStartTime_ThrowsException() {
        request.setEndTime(request.getStartTime().minusHours(1));
        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(request, 1L));
    }

    @Test
    void testGetEventById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        var response = eventService.getEventById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void testGetEventById_NotFound_ThrowsException() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> eventService.getEventById(999L));
    }

    @Test
    void testGetEventsByStatus_Success() {
        when(eventRepository.findByStatus(Event.EventStatus.ACTIVE)).thenReturn(Arrays.asList(event));
        var response = eventService.getEventsByStatus("ACTIVE");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testGetAnOrganizerAllEventsByOrganizerId_Success() {
        when(eventRepository.findByOrganizerId(1L)).thenReturn(Arrays.asList(event));
        var response = eventService.getAnOrganizerAllEventsByOrganizerId(1L);
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testGetEventsByOrganizerAndStatus_Success() {
        when(eventRepository.findByOrganizerIdAndStatus(1L, Event.EventStatus.ACTIVE))
                .thenReturn(Arrays.asList(event));
        var response = eventService.getEventsByOrganizerAndStatus(1L, "ACTIVE");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testCancelEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.findByEventId(1L)).thenReturn(Arrays.asList());
        assertDoesNotThrow(() -> eventService.cancelEvent(1L, 1L));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCancelEvent_Unauthorized_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        assertThrows(RuntimeException.class, () -> eventService.cancelEvent(1L, 999L));
    }

    @Test
    void testCancelEvent_AlreadyCompleted_ThrowsException() {
        event.setStatus(Event.EventStatus.COMPLETED);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        assertThrows(RuntimeException.class, () -> eventService.cancelEvent(1L, 1L));
    }

    @Test
    void testConvertToDTO() {
        var dto = eventService.convertToDTO(event);
        assertEquals(event.getId(), dto.getId());
        assertEquals(event.getName(), dto.getName());
    }
}