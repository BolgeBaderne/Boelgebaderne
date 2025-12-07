package com.example.bolgebaderne.controller;


import com.example.bolgebaderne.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes { //hjælpeklasse!

    @ModelAttribute("role")
    public String addRole(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return "GUEST"; // ikke logget ind
        }

        // Hvis din User har en enum Role (MEMBER, NON_MEMBER, ADMIN)
        return currentUser.getRole().name();

        // Hvis feltet er en String, brug i stedet:
        // return currentUser.getRole();
    }
}
