package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberBookingPageController {

    @GetMapping("/booking")
    public String bookingPage(@AuthenticationPrincipal User user, Model model) {

        // Hvis brugeren ikke er logget ind, viser vi stadig booking-siden (guest mode)
        boolean isLoggedIn = (user != null);
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (!isLoggedIn) {
            model.addAttribute("userId", null);
            model.addAttribute("userName", "");
            return "booking";
        }

        // Læg user-info i modellen til booking.html
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userName", user.getName());

        return "booking";
    }
}
