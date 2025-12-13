package com.example.bolgebaderne.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SaunaEventPageController {

    @GetMapping("/events/view")
    public String eventViewPage() {
        return "events/event";
    }
}
