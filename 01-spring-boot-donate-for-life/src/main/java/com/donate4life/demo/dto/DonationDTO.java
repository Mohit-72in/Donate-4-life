package com.donate4life.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationDTO {
    private Integer donationId;
    private Integer donorId;
    private LocalDate donationDate;
    private String hospitalName;
}