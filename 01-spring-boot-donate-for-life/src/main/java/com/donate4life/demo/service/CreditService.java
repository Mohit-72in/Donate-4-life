package com.donate4life.demo.service;

import com.donate4life.demo.entity.Credit;
import com.donate4life.demo.repository.CreditRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class CreditService {

    private final CreditRepository creditRepository;

    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    public List<Credit> getAllCredits() {
        return creditRepository.findAll();
    }

    public List<Credit> getCreditsByDonor(Integer donorId) {
        return creditRepository.findByDonor_UserId(donorId);
    }

    public List<Credit> getValidCreditsByDonor(Integer donorId) {
        return creditRepository.findByDonor_UserIdAndCreditExpiryDateAfter(donorId, LocalDate.now());
    }

    // This is the new method we added
    public long getValidCreditCountByDonor(Integer donorId) {
        return creditRepository.findByDonor_UserIdAndCreditExpiryDateAfter(donorId, LocalDate.now()).size();
    }
}