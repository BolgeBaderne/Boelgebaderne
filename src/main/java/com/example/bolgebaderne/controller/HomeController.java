package com.example.bolgebaderne.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

        @GetMapping("/")
        public String root() {
            return "forward:/index.html"; // src/main/resources/static/index.html
        }

        @GetMapping("/home")
        public String home() {
            return "home"; // src/main/resources/templates/home.html
        }
    }


