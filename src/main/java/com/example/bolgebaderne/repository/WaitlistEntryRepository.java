package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.model.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry, Integer> {
    List<WaitlistEntry> findBySaunaEventOrderByPositionAsc(SaunaEvent saunaEvent);
    boolean existsByUserAndEvent(User user, SaunaEvent saunaEvent);
    int countBySaunaEvent(SaunaEvent saunaEvent);
}

