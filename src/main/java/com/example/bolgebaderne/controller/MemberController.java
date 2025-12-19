package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.dto.BookingResponseDTO;
import com.example.bolgebaderne.dto.MemberProfileResponseDTO;
import com.example.bolgebaderne.dto.ShiftResponseDTO;
import com.example.bolgebaderne.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.bolgebaderne.service.MemberProfileService;
import com.example.bolgebaderne.service.MemberQuickBookingService;
import com.example.bolgebaderne.service.MemberShiftService;

import java.util.List;

@RestController
@RequestMapping("/member")
@PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
public class MemberController {

    private final MemberProfileService memberProfileService;
    private final MemberQuickBookingService quickBookingService;
    private final MemberShiftService shiftService;

    public MemberController(MemberProfileService memberProfileService,
                            MemberQuickBookingService quickBookingService,
                            MemberShiftService shiftService) {
        this.memberProfileService = memberProfileService;
        this.quickBookingService = quickBookingService;
        this.shiftService = shiftService;
    }

//// Simpel profil-endpoint til test uden login
//    @GetMapping("/profile")
//    public MemberProfileResponseDTO getProfile(
//        @RequestParam(required = false) String email
//) {
//    if (email == null || email.isBlank()) {
//        email = "member1@example.com";  // default test-bruger
//    }
//    return memberProfileService.getProfile(email);
//}
   @GetMapping("/profile")
   public MemberProfileResponseDTO getProfile(@AuthenticationPrincipal User currentUser) {
    return memberProfileService.getProfile(currentUser.getEmail());

   }

    @GetMapping("/profiles")
   public List<MemberProfileResponseDTO> getAllProfiles() {
       return memberProfileService.getAllProfiles();
  }

    @GetMapping("/quick-booking")
    public List<BookingResponseDTO> getQuickBookings() {
        return quickBookingService.getQuickBookings();
    }

    @GetMapping("/shifts")
    public List<ShiftResponseDTO> getShifts() {
        return shiftService.getShifts();
    }
}
