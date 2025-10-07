package com.donate4life.demo.controller;

import com.donate4life.demo.dto.DonationDTO;
import com.donate4life.demo.entity.Donation;
import com.donate4life.demo.service.DonationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    // ⭐ REMOVED: The @PostMapping for addDonation has been removed
    // as this functionality is now handled by DonationPageController.

    @GetMapping
    public List<DonationDTO> getAllDonations() {
        return donationService.getAllDonations().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/donor/{donorId}")
    public List<DonationDTO> getDonationsByDonor(@PathVariable Integer donorId) {
        return donationService.getDonationsByDonor(donorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DonationDTO convertToDto(Donation donation) {
        return new DonationDTO(
                donation.getDonationId(),
                donation.getDonor().getUserId(),
                donation.getDonationDate(),
                donation.getHospitalName()
        );
    }
}