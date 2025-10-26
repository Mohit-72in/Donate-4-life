package com.donate4life.demo.repository;

import com.donate4life.demo.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

    @Query("SELECT d FROM Donation d JOIN FETCH d.donor")
    List<Donation> findAllWithDonors();

    List<Donation> findByDonor_UserId(Integer donorId);

    // Finds unverified donations and their donors for the admin dashboard
    @Query("SELECT d FROM Donation d JOIN FETCH d.donor WHERE d.verificationStatus = :status ORDER BY d.donationDate ASC")
    List<Donation> findByVerificationStatusWithDonor(Donation.VerificationStatus status);

    //  NEW: This method solves the error on the verification details page
    @Query("SELECT d FROM Donation d JOIN FETCH d.donor WHERE d.donationId = :donationId")
    Optional<Donation> findByIdWithDonor(Integer donationId);

    // This method is the key for the 90-day rule
    Optional<Donation> findTopByDonor_UserIdAndVerificationStatusOrderByDonationDateDesc(Integer donorId, Donation.VerificationStatus status);

}