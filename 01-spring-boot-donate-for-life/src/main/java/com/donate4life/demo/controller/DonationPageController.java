package com.donate4life.demo.controller;

import com.donate4life.demo.entity.Donation;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.service.DonationService;
import com.donate4life.demo.service.FileStorageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
public class DonationPageController {

    private final DonationService donationService;
    private final FileStorageService fileStorageService;

    public DonationPageController(DonationService donationService, FileStorageService fileStorageService) {
        this.donationService = donationService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/add-donation")
    public String showDonationForm(Model model, @AuthenticationPrincipal User currentUser) {
        Optional<Donation> latestDonationOpt = donationService.findLatestApprovedDonation(currentUser.getUserId());

        if (latestDonationOpt.isPresent()) {
            Donation latestDonation = latestDonationOpt.get();
            long daysSinceLastDonation = ChronoUnit.DAYS.between(latestDonation.getDonationDate(), LocalDate.now());

            if (daysSinceLastDonation < 90) {
                model.addAttribute("isEligible", false);
                model.addAttribute("nextEligibleDate", latestDonation.getDonationDate().plusDays(90));
                return "add-donation";
            }
        }

        model.addAttribute("isEligible", true);
        model.addAttribute("donation", new Donation());
        return "add-donation";
    }

    @PostMapping("/add-donation")
    public String processDonation(@ModelAttribute Donation donation,
                                  @RequestParam("document") MultipartFile document,
                                  @AuthenticationPrincipal User currentUser,
                                  RedirectAttributes redirectAttributes) {

        Optional<Donation> latestDonationOpt = donationService.findLatestApprovedDonation(currentUser.getUserId());
        if (latestDonationOpt.isPresent()) {
            long daysSinceLastDonation = ChronoUnit.DAYS.between(latestDonationOpt.get().getDonationDate(), LocalDate.now());
            if (daysSinceLastDonation < 90) {
                redirectAttributes.addFlashAttribute("error", "You are not yet eligible to record a new donation.");
                return "redirect:/add-donation";
            }
        }

        if (document.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Proof of donation is required.");
            return "redirect:/add-donation";
        }

        String documentUrl = fileStorageService.save(document);
        donationService.addDonation(currentUser.getUserId(), donation.getDonationDate(), donation.getHospitalName(), documentUrl);

        redirectAttributes.addFlashAttribute("success", "Thank you! Your donation has been submitted for verification.");
        return "redirect:/profile";
    }
}