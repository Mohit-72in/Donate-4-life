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
@Table(name = "donations")
public class Donation {

    // ⭐ NEW: Verification Status Enum
    public enum VerificationStatus { UNVERIFIED, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_id")
    private Integer donationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @Column(name = "donation_date", nullable = false)
    private LocalDate donationDate;

    @Column(name = "hospital_name", length = 100)
    private String hospitalName;

    // ⭐ NEW: Field for verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    // ⭐ NEW: Field for the proof document URL
    @Column(name = "document_url")
    private String documentUrl;

    public Donation(User donor, LocalDate donationDate, String hospitalName) {
        this.donor = donor;
        this.donationDate = donationDate;
        this.hospitalName = hospitalName;
    }
}