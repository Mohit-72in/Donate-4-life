package com.donate4life.demo.dto;

import com.donate4life.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // Generates a constructor with all fields
public class CompatibleDonorDTO {
    private User donor;
    private double distanceInKm;
    private boolean isExactMatch;
}