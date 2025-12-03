package com.example.bolgebaderne.service;

import com.example.bolgebaderne.dto.MemberProfileResponseDTO;
import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;   // VIGTIG import

@Service
public class MemberProfileService {

    private final UserRepository userRepository;

    public MemberProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1) ÉN profil ud fra email
    public MemberProfileResponseDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return new MemberProfileResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getMembershipStatus(),
                user.getRole().name()
        );
    }

    // 2) ALLE profiler
    public List<MemberProfileResponseDTO> getAllProfiles() {
        return userRepository.findAll()      // henter alle User-entities fra DB
                .stream()                    // laver dem om til en stream
                .map(user -> new MemberProfileResponseDTO(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getMembershipStatus(),
                        user.getRole().name()
                ))
                .toList();                   // <- husk .toList() med stort L
    }
}
