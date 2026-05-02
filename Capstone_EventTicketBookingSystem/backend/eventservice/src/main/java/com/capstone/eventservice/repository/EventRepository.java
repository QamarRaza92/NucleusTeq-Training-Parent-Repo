package com.capstone.eventservice.repository;

import com.capstone.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event,Long>
{
    List<Event> findByOrganizerId(Long id);
    List<Event> findByEventDate(LocalDateTime now);
    List<Event> findByEventDateAfter(LocalDateTime date);
    List<Event> findByEventDateBefore(LocalDateTime date);
    List<Event> findByOrganizerIdAndStatus(Long organizerId, Event.EventStatus status);
    List<Event> findByStatus(Event.EventStatus status);
}