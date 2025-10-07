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
public class CreditDTO {
    private Integer creditId;
    private Integer donorId;
    private LocalDate creditEarnedDate;
    private LocalDate creditExpiryDate;
}