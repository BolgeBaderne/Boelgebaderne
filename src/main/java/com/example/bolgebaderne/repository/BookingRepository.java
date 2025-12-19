package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    int countBySaunaEvent(SaunaEvent saunaEvent);
    // Added method to count bookings by event ID
    long countBySaunaEvent_EventId(int eventId);
    // Added method to check if a booking exists for a given user ID and event ID
    boolean existsByUser_UserIdAndSaunaEvent_EventId(int userId, int eventId);

    // Dashboard
    List<Booking> findByUser_UserId(int userId);

    void deleteBySaunaEvent_EventId(int eventId);
}
