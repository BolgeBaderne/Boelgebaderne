package com.example.bolgebaderne.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/events")
@PreAuthorize("hasRole('ADMIN')")

public class AdminSaunaEventPageController {
    @GetMapping
    public String adminEventsPage() {
        return "admin/admin-events";
    }

    @GetMapping("/{id}")
    public String adminEventByIdPage(@PathVariable int id, Model model) {
        model.addAttribute("eventId", id);
        return "admin/admin-events";
    }
}

