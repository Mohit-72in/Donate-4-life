package com.donate4life.demo.repository;

import com.donate4life.demo.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Integer> {

    List<Credit> findByDonor_UserId(Integer donorId);

    List<Credit> findByDonor_UserIdAndCreditExpiryDateAfter(Integer donorId, LocalDate date);

    // ⭐ CORRECTED METHOD NAME
    // The underscore tells Spring to look inside the 'donation' object for the 'donationId' field.
    boolean existsByDonation_DonationId(Integer donationId);
}