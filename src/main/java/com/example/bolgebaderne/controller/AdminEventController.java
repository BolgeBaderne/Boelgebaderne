//package com.example.bolgebaderne.controller;
//
//import com.example.bolgebaderne.dto.EventDTO;
//import com.example.bolgebaderne.model.SaunaEvent;
//import com.example.bolgebaderne.service.SaunaEventService;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin/events")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminEventController {
//
//    private final SaunaEventService saunaEventService;
//
//    public AdminEventController(SaunaEventService saunaEventService) {
//        this.saunaEventService = saunaEventService;
//    }
//
//    @GetMapping
//    public List<EventDTO> getAllForAdmin() {
//        return saunaEventService.getAllEvents()
//                .stream()
//                .map(this::toDTO)
//                .toList();
//    }
//
//    @GetMapping("/{id}")
//    public EventDTO getEvent(@PathVariable int id) {
//        return toDTO(saunaEventService.getById(id));
//    }
//
//    @PostMapping
//    public EventDTO create(@RequestBody EventAdminRequest dto) {
//        return toDTO(saunaEventService.createEvent(dto));
//    }
//
//    @PutMapping("/{id}")
//    public EventDTO update(@PathVariable int id, @RequestBody EventAdminRequest dto) {
//        return toDTO(saunaEventService.updateEvent(id, dto));
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable int id) {
//        saunaEventService.deleteEvent(id);
//    }
//
//
//    }
//
//
