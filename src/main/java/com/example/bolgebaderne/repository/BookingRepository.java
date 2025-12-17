package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.Booking;
import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    int countBySaunaEvent(SaunaEvent saunaEvent);

    long countByEvent_EventId(int eventId);

    boolean existsByUser_UserIdAndEvent_EventId(int userId, int eventId);
}
