package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberBookingPageController {

    @GetMapping("/booking")
    public String bookingPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userName", user.getName());
        return "booking";
    }
}
