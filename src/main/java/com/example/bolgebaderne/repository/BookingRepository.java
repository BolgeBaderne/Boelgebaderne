package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    int countBySaunaEvent(SaunaEvent saunaEvent);

    // Fixed: use property name 'saunaEvent' (matches Booking.saunaEvent) for nested property lookup
    long countBySaunaEvent_EventId(int eventId);

    // Corrected method name to reference SaunaEvent property (was referencing non-existent 'event')
    boolean existsByUser_UserIdAndSaunaEvent_EventId(int userId, int eventId);

    void deleteBySaunaEvent_EventId(int eventId);
}
