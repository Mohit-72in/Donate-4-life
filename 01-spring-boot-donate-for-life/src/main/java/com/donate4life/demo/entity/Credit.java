package com.donate4life.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "credits")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_id")
    private Integer creditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @Column(name = "credit_earned_date", nullable = false)
    private LocalDate creditEarnedDate;

    @Column(name = "credit_expiry_date", nullable = false)
    private LocalDate creditExpiryDate;

    // Link back to the donation that earned this credit
    @OneToOne
    @JoinColumn(name = "donation_id", unique = true)
    private Donation donation;

    // The old constructor has been removed as it is no longer used.
}