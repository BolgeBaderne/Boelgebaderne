package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    long countByEvent_EventId(int eventId);

    boolean existsByUser_UserIdAndEvent_EventId(int userId, int eventId);
}
