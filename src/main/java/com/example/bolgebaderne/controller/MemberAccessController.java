package com.example.bolgebaderne.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberAccessController {

    @GetMapping("/membership-required")
    public String membershipRequiredPage() {
        // peger på src/main/resources/templates/membership-required.html
        return "membership-required";
    }
}

