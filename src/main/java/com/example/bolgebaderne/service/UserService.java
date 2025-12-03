package com.example.bolgebaderne.service;


import com.example.bolgebaderne.model.User;
import org.springframework.stereotype.Service;
import com.example.bolgebaderne.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
