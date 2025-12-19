package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal User user) {
        return Map.of(
                "name", user.getName(),
                "role", user.getRole().name()
        );
    }
}
