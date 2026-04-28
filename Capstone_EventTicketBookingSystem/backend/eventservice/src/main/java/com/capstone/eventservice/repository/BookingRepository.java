package com.capstone.eventservice.repository;

import com.capstone.eventservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository <Booking,Long>
{
    List<Booking> findByAudienceId(Long audienceId);
    List<Booking> findByEventId(Long eventId);
    List<Booking> findByEventIdIn(List<Long> eventIds);
    Optional<Booking> findByIdAndAudienceId(Long id, Long audienceId);
    List<Booking> findByAudienceIdAndStatus(Long audienceId, String status);
    void deleteByEventId(Long eventId);
    void deleteByIdAndAudienceId(Long id,Long audienceId);
    
}