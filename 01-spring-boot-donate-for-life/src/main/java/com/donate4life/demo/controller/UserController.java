package com.donate4life.demo.controller;

import com.donate4life.demo.entity.Donation;
import com.donate4life.demo.entity.Request;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.service.CreditService;
import com.donate4life.demo.service.DonationService;
import com.donate4life.demo.service.RequestService;
import com.donate4life.demo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final CreditService creditService;
    private final DonationService donationService; // 1. Declare the new services
    private final RequestService requestService;

    // 2. Inject the services in the constructor
    public UserController(UserService userService, CreditService creditService, DonationService donationService, RequestService requestService) {
        this.userService = userService;
        this.creditService = creditService;
        this.donationService = donationService;
        this.requestService = requestService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "donor";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        User savedUser = userService.registerUser(user);
        redirectAttributes.addAttribute("userId", savedUser.getUserId());
        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String showOtpVerificationPage(@RequestParam("userId") Integer userId, Model model) {
        model.addAttribute("userId", userId);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processOtpVerification(@RequestParam("userId") Integer userId,
                                         @RequestParam("otp") String otp,
                                         RedirectAttributes redirectAttributes) {
        boolean isVerified = userService.verifyOtp(userId, otp);

        if (isVerified) {
            redirectAttributes.addFlashAttribute("success", "Verification successful! Please log in.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP. Please try again.");
            redirectAttributes.addAttribute("userId", userId);
            return "redirect:/verify-otp";
        }
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("user", currentUser);

        if (currentUser.getUserType() == User.UserType.DONOR) {
            // Get credit count
            long creditCount = creditService.getValidCreditCountByDonor(currentUser.getUserId());
            model.addAttribute("creditCount", creditCount);

            // Get donation history
            List<Donation> donationHistory = donationService.getDonationsByDonor(currentUser.getUserId());
            model.addAttribute("donationHistory", donationHistory);

        } else if (currentUser.getUserType() == User.UserType.ACCEPTOR) {
            // Get request history
            List<Request> requestHistory = requestService.getRequestsByAcceptor(currentUser.getUserId());
            model.addAttribute("requestHistory", requestHistory);
        }

        return "profile";
    }
}