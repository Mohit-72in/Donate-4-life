package com.donate4life.demo.service;

import com.donate4life.demo.entity.Credit;
import com.donate4life.demo.entity.Donation;
import com.donate4life.demo.entity.User;
import com.donate4life.demo.repository.CreditRepository;
import com.donate4life.demo.repository.DonationRepository;
import com.donate4life.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final CreditRepository creditRepository;
    private final EmailService emailService; // For rejection notifications

    public DonationService(DonationRepository donationRepository, UserRepository userRepository, CreditRepository creditRepository, EmailService emailService) {
        this.donationRepository = donationRepository;
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Donation addDonation(Integer donorId, LocalDate donationDate, String hospitalName, String documentUrl,
                                Double latitude, Double longitude) { // ⭐ ADD PARAMETERS
        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found with ID: " + donorId));

        Donation donation = new Donation(donor, donationDate, hospitalName);
        donation.setDocumentUrl(documentUrl);

        // ⭐ ADD THESE TWO LINES
        donation.setLatitude(latitude);
        donation.setLongitude(longitude);

        return donationRepository.save(donation);
    }

    public List<Donation> getUnverifiedDonations() {
        return donationRepository.findByVerificationStatusWithDonor(Donation.VerificationStatus.UNVERIFIED);
    }

    @Transactional
    public void verifyDonation(Integer donationId) {
        Donation donation = donationRepository.findByIdWithDonor(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (creditRepository.existsByDonation_DonationId(donationId)) {
            return; // Already verified, do nothing
        }

        donation.setVerificationStatus(Donation.VerificationStatus.APPROVED);
        donationRepository.save(donation);

        Credit credit = new Credit();
        credit.setDonation(donation);
        credit.setDonor(donation.getDonor());
        credit.setCreditEarnedDate(donation.getDonationDate());
        credit.setCreditExpiryDate(donation.getDonationDate().plusYears(1));
        creditRepository.save(credit);
    }

    public Donation getDonationById(Integer donationId) {
        return donationRepository.findByIdWithDonor(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + donationId));
    }

    // ⭐ Method to reject a donation and notify the donor
    public void rejectDonation(Integer donationId, String reason) {
        Donation donation = donationRepository.findByIdWithDonor(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        donation.setVerificationStatus(Donation.VerificationStatus.REJECTED);
        donationRepository.save(donation);

        emailService.sendDonationRejectionEmail(
                donation.getDonor().getUsername(),
                donation.getDonor().getName(),
                donation.getDonationDate(),
                reason
        );
    }

    // ⭐ Method to find the last approved donation for the 90-day cooldown rule
    public Optional<Donation> findLatestApprovedDonation(Integer donorId) {
        return donationRepository.findTopByDonor_UserIdAndVerificationStatusOrderByDonationDateDesc(
                donorId, Donation.VerificationStatus.APPROVED
        );
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAllWithDonors();
    }

    public List<Donation> getDonationsByDonor(Integer donorId) {
        return donationRepository.findByDonor_UserId(donorId);
    }
}