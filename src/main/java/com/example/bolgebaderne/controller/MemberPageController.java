package com.example.bolgebaderne.controller;

import com.example.bolgebaderne.model.User;
import com.example.bolgebaderne.service.MemberProfileService;
import com.example.bolgebaderne.service.MemberQuickBookingService;
import com.example.bolgebaderne.service.MemberShiftService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/member")
@PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
public class MemberPageController {

    private final MemberProfileService memberProfileService;
    private final MemberQuickBookingService quickBookingService;
    private final MemberShiftService shiftService;

    public MemberPageController(MemberProfileService memberProfileService,
                                MemberQuickBookingService quickBookingService,
                                MemberShiftService shiftService) {
        this.memberProfileService = memberProfileService;
        this.quickBookingService = quickBookingService;
        this.shiftService = shiftService;
    }

    // 1) Profil-side
    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal User currentUser,
                              Model model) {

        // ikke logget ind eller ikke medlem → vis medlemskrav-side
        if (currentUser == null || !currentUser.isMember()) {
            return "redirect:/membership-required";
        }

        var profileDto = memberProfileService.getProfile(currentUser.getEmail());
        model.addAttribute("profile", profileDto);

        return "member/profile";   // -> templates/member/profile.html
    }

    // 2) Hurtig booking
    @GetMapping("/quick-booking")
    public String quickBookingPage(@AuthenticationPrincipal User currentUser,
                                   Model model) {

        if (currentUser == null || !currentUser.isMember()) {
            return "redirect:/membership-required";
        }

        var bookings = quickBookingService.getQuickBookings();
        model.addAttribute("bookings", bookings);

        return "member/quick-booking"; // -> templates/member/quick-booking.html
    }

    // 3) Vagtplan
    @GetMapping("/shifts")
    public String shiftsPage(@AuthenticationPrincipal User currentUser,
                             Model model) {

        if (currentUser == null || !currentUser.isMember()) {
            return "redirect:/membership-required";
        }

        var shifts = shiftService.getShifts();
        model.addAttribute("shifts", shifts);

        return "member/shifts";   // -> templates/member/shifts.html
    }
}
