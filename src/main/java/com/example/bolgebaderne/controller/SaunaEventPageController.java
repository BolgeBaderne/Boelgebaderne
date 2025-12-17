package com.example.bolgebaderne.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SaunaEventPageController {

    @GetMapping("/api/events/view")
    public String eventViewPage() {
        return "events/event";
    }

    @GetMapping("/api/events/view/{id}")
    public String eventViewPageById(@PathVariable int id, Model model) {
        model.addAttribute("eventId", id);
        return "events/event";
    }
}
