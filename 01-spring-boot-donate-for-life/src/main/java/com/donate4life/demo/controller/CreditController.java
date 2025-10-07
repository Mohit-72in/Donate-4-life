package com.donate4life.demo.controller;

import com.donate4life.demo.dto.CreditDTO;
import com.donate4life.demo.service.CreditService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping
    public List<CreditDTO> getAllCredits() {
        return creditService.getAllCredits().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/donor/{donorId}")
    public List<CreditDTO> getCreditsByDonor(@PathVariable Integer donorId) {
        return creditService.getCreditsByDonor(donorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/donor/{donorId}/valid")
    public List<CreditDTO> getValidCreditsByDonor(@PathVariable Integer donorId) {
        return creditService.getValidCreditsByDonor(donorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CreditDTO convertToDto(com.donate4life.demo.entity.Credit credit) {
        return new CreditDTO(
                credit.getCreditId(),
                credit.getDonor().getUserId(),
                credit.getCreditEarnedDate(),
                credit.getCreditExpiryDate()
        );
    }
}