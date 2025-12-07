package com.example.bolgebaderne.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberPageController {

    @GetMapping("/profile")
    public String profilePage() {
        // peger på src/main/resources/templates/member/profile.html
        return "member/profile";
    }

    @GetMapping("/quick-booking")
    public String quickBookingPage() {
        return "member/quick-booking";
    }

    @GetMapping("/shifts")
    public String shiftsPage() {
        return "member/shifts";
    }
}
