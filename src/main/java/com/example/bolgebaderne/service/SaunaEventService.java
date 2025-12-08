package com.example.bolgebaderne.service;

import com.example.bolgebaderne.exceptions.EventNotFoundException;
import com.example.bolgebaderne.model.SaunaEvent;
import com.example.bolgebaderne.repository.SaunaEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaunaEventService {

    private final SaunaEventRepository repository;

    public SaunaEventService(SaunaEventRepository repository) {
        this.repository = repository;
    }

    public List<SaunaEvent> getAllEvents() {
        return repository.findAll();
    }

    public SaunaEvent getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Det valgte event findes ikke."));
    }
}
