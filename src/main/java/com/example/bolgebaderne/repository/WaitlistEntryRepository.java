package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.model.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry, Integer> {
    List<WaitlistEntry> findBySaunaEventOrderByPositionAsc(SaunaEvent saunaEvent);
    int countBySaunaEvent(SaunaEvent saunaEvent);
    boolean existsByUserAndSaunaEvent(User user, SaunaEvent saunaEvent);
    Optional<WaitlistEntry> findByUserAndSaunaEvent(User user, SaunaEvent saunaEvent);

    void deleteBySaunaEvent_EventId(int eventId);
}









