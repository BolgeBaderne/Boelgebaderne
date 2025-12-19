package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SaunaEventRepository extends JpaRepository<SaunaEvent, Integer> {
    // Bruges til at finde et event ud fra titel + starttid,
    Optional<SaunaEvent> findByTitleAndStartTime(String title, LocalDateTime startTime);
}
