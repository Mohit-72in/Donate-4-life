package com.donate4life.demo.controller;

import com.donate4life.demo.entity.Donation;
import com.donate4life.demo.entity.Request;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.service.DonationService;
import com.donate4life.demo.service.EmailService;
import com.donate4life.demo.service.RequestService;
import com.donate4life.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Updated import
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RequestService requestService;
    private final UserService userService;
    private final EmailService emailService;
    private final DonationService donationService;

    public AdminController(RequestService requestService, UserService userService, EmailService emailService, DonationService donationService) {
        this.requestService = requestService;
        this.userService = userService;
        this.emailService = emailService;
        this.donationService = donationService;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("activeRequests", requestService.getPendingRequests());
        model.addAttribute("unverifiedRequests", requestService.getUnverifiedRequests());
        model.addAttribute("unverifiedDonations", donationService.getUnverifiedDonations());
        return "admin/dashboard";
    }

    // --- Request Management ---

    @GetMapping("/request/{requestId}")
    public String showRequestDetails(@PathVariable Integer requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        List<User> compatibleDonors = userService.findCompatibleDonors(request.getRequestedBloodGroup());
        model.addAttribute("compatibleDonors", compatibleDonors);
        return "admin/view-request";
    }

    @GetMapping("/request/verify/{requestId}")
    public String showVerificationPage(@PathVariable Integer requestId, Model model) {
        model.addAttribute("request", requestService.getRequestById(requestId));
        return "admin/verify-request";
    }

    @PostMapping("/request/approve/{requestId}")
    public String approveRequest(@PathVariable Integer requestId, RedirectAttributes redirectAttributes) {
        requestService.verifyRequest(requestId, true);
        redirectAttributes.addFlashAttribute("success", "Request has been approved and is now active.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/request/reject/{requestId}")
    public String rejectRequest(@PathVariable Integer requestId, RedirectAttributes redirectAttributes) {
        requestService.verifyRequest(requestId, false);
        redirectAttributes.addFlashAttribute("success", "Request has been rejected.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/request/{requestId}/notify/{donorId}")
    public String notifyDonor(@PathVariable Integer requestId,
                              @PathVariable Integer donorId,
                              RedirectAttributes redirectAttributes) {
        Request bloodRequest = requestService.getRequestById(requestId);
        User donor = userService.getUserById(donorId);
        emailService.sendDonationRequestEmail(
                donor.getUsername(),
                donor.getName(),
                bloodRequest.getAcceptor().getName(),
                bloodRequest.getHospitalName(),
                bloodRequest.getAcceptor().getPhone()
        );
        redirectAttributes.addFlashAttribute("success", "Donor '" + donor.getName() + "' has been notified successfully!");
        return "redirect:/admin/request/" + requestId;
    }

    // --- Donation Management ---

    @GetMapping("/donation/verify/{donationId}")
    public String showDonationVerificationPage(@PathVariable Integer donationId, Model model) {
        model.addAttribute("donation", donationService.getDonationById(donationId));
        return "admin/verify-donation";
    }

    @PostMapping("/donation/approve/{donationId}")
    public String approveDonation(@PathVariable Integer donationId, RedirectAttributes redirectAttributes) {
        donationService.verifyDonation(donationId);
        redirectAttributes.addFlashAttribute("success", "Donation has been approved and credit awarded!");
        return "redirect:/admin/dashboard";
    }

    // ⭐ UPDATED: To accept the rejection reason from the form
    @PostMapping("/donation/reject/{donationId}")
    public String rejectDonation(@PathVariable Integer donationId,
                                 @RequestParam("rejectionReason") String reason,
                                 RedirectAttributes redirectAttributes) {
        donationService.rejectDonation(donationId, reason);
        redirectAttributes.addFlashAttribute("success", "Donation has been rejected and the donor has been notified.");
        return "redirect:/admin/dashboard";
    }
}