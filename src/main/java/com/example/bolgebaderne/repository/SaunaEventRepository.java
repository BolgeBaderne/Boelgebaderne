package com.example.bolgebaderne.repository;

import com.example.bolgebaderne.model.SaunaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaunaEventRepository extends JpaRepository<SaunaEvent, Integer> {
}
