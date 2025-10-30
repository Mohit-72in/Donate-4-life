package com.donate4life.demo.controller;

import com.donate4life.demo.dto.CompatibleDonorDTO;
import com.donate4life.demo.entity.Donation;
import com.donate4life.demo.entity.Request;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.service.DonationService;
import com.donate4life.demo.service.EmailService;
import com.donate4life.demo.service.RequestService;
import com.donate4life.demo.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RequestService requestService;
    private final UserService userService;
    private final EmailService emailService;
    private final DonationService donationService;

    // This is for the details page, not the verify page
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

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

    // THIS IS FOR "View Details" (Active Requests)
    @GetMapping("/request/{requestId}")
    public String showRequestDetails(@PathVariable Integer requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        System.out.println("CONTROLLER DEBUG: Request ID=" + requestId + ", Blood Group=" + request.getRequestedBloodGroup());
        model.addAttribute("request", request);

        List<User> compatibleDonorsRaw = userService.findCompatibleDonors(request.getRequestedBloodGroup());
        System.out.println("CONTROLLER DEBUG: Raw Donors Found by Service=" + compatibleDonorsRaw.size());

        Double requestLat = request.getLatitude();
        Double requestLng = request.getLongitude();

        List<CompatibleDonorDTO> donorDtos = compatibleDonorsRaw.stream()
                .map(donor -> {
                    Double donorLat = donor.getLatitude();
                    Double donorLng = donor.getLongitude();
                    double distance = -1.0;

                    if (requestLat != null && requestLng != null && donorLat != null && donorLng != null) {
                        distance = userService.haversine(requestLat, requestLng, donorLat, donorLng);
                    }
                    boolean exactMatch = donor.getBloodGroup().equals(request.getRequestedBloodGroup());
                    return new CompatibleDonorDTO(donor, distance, exactMatch);
                })
                .sorted(Comparator.comparingDouble((CompatibleDonorDTO dto) -> dto.getDistanceInKm() < 0 ? Double.MAX_VALUE : dto.getDistanceInKm()))
                .collect(Collectors.toList());

        System.out.println("CONTROLLER DEBUG: Sorted DTOs count=" + donorDtos.size());
        model.addAttribute("compatibleDonors", donorDtos);

        // This page does NOT need the API key since we removed the map
        // model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        return "admin/view-request"; // This correctly points to the "details" page
    }

    // ⭐ THIS IS FOR "Verify" (Pending Requests) ⭐
    @GetMapping("/request/verify/{requestId}")
    public String showVerificationPage(@PathVariable Integer requestId, Model model) {
        try {
            Request request = requestService.getRequestById(requestId);
            model.addAttribute("request", request);

            // ⭐ THIS IS THE FIX. It must return "verify-request" ⭐
            return "admin/verify-request";

        } catch (Exception e) {
            System.err.println("Error showing request verify page for ID " + requestId + ": " + e.getMessage());
            return "redirect:/admin/dashboard?error=RequestNotFound";
        }
    }


    @PostMapping("/request/approve/{requestId}")
    public String approveRequest(@PathVariable Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            requestService.verifyRequest(requestId, true);
            redirectAttributes.addFlashAttribute("success", "Request has been approved and is now active.");
        } catch (Exception e) {
            System.err.println("Error approving request " + requestId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to approve request.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/request/reject/{requestId}")
    public String rejectRequest(@PathVariable Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            requestService.verifyRequest(requestId, false);
            redirectAttributes.addFlashAttribute("success", "Request has been rejected.");
        } catch (Exception e) {
            System.err.println("Error rejecting request " + requestId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to reject request.");
        }
        return "redirect:/admin/dashboard";
    }

    // This button is for the "view-request" page
    @PostMapping("/request/fulfill/{requestId}")
    public String fulfillRequest(@PathVariable Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            requestService.fulfillRequest(requestId);
            redirectAttributes.addFlashAttribute("success", "Request has been marked as FULFILLED and removed from the active list.");
        } catch (Exception e) {
            System.err.println("Error fulfilling request " + requestId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error fulfilling request.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/request/{requestId}/notify/{donorId}")
    public String notifyDonor(@PathVariable Integer requestId,
                              @PathVariable Integer donorId,
                              RedirectAttributes redirectAttributes) {
        try {
            Request bloodRequest = requestService.getRequestById(requestId);
            User donor = userService.getUserById(donorId);
            if (bloodRequest.getAcceptor() == null) {
                throw new IllegalStateException("Acceptor details missing for request ID: " + requestId);
            }
            emailService.sendDonationRequestEmail(
                    donor.getUsername(),
                    donor.getName(),
                    bloodRequest.getAcceptor().getName(),
                    bloodRequest.getHospitalName(),
                    bloodRequest.getAcceptor().getPhone()
            );
            redirectAttributes.addFlashAttribute("success", "Donor '" + donor.getName() + "' has been notified successfully!");
        } catch (Exception e) {
            System.err.println("Error notifying donor " + donorId + " for request " + requestId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to notify donor: " + e.getMessage());
        }
        return "redirect:/admin/request/" + requestId;
    }

    // --- Donation Management ---
    // (All donation methods are correct and do not need changes)

    @GetMapping("/donation/verify/{donationId}")
    public String showDonationVerificationPage(@PathVariable Integer donationId, Model model) {
        try {
            Donation donation = donationService.getDonationById(donationId);
            model.addAttribute("donation", donation);
            return "admin/verify-donation";
        } catch (Exception e) {
            System.err.println("Error showing donation verify page for ID " + donationId + ": " + e.getMessage());
            return "redirect:/admin/dashboard?error=DonationNotFound";
        }
    }

    @PostMapping("/donation/approve/{donationId}")
    public String approveDonation(@PathVariable Integer donationId, RedirectAttributes redirectAttributes) {
        try {
            donationService.verifyDonation(donationId);
            redirectAttributes.addFlashAttribute("success", "Donation has been approved and credit awarded!");
        } catch (Exception e) {
            System.err.println("Error approving donation " + donationId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to approve donation.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/donation/reject/{donationId}")
    public String rejectDonation(@PathVariable Integer donationId,
                                 @RequestParam("rejectionReason") String reason,
                                 RedirectAttributes redirectAttributes) {
        if (reason == null || reason.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rejection reason cannot be empty.");
            return "redirect:/admin/donation/verify/" + donationId;
        }
        try {
            donationService.rejectDonation(donationId, reason);
            redirectAttributes.addFlashAttribute("success", "Donation has been rejected and the donor has been notified.");
        } catch (Exception e) {
            System.err.println("Error rejecting donation " + donationId + ": " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to reject donation.");
        }
        return "redirect:/admin/dashboard";
    }
}