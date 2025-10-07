package com.donate4life.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {

    public enum Status { PENDING, FULFILLED, CANCELLED }

    // ⭐ NEW: Verification Status Enum
    public enum VerificationStatus { UNVERIFIED, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptor_id", nullable = false)
    private User acceptor;

    @Column(name = "requested_blood_group", length = 10)
    private String requestedBloodGroup;

    @CreationTimestamp
    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDateTime requestDate;

    @Column(name = "hospital_name", length = 100)
    private String hospitalName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    // ⭐ NEW: Fields for verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "document_url")
    private String documentUrl; // To store the path to the uploaded document

    public Request(User acceptor, String requestedBloodGroup, String hospitalName) {
        this.acceptor = acceptor;
        this.requestedBloodGroup = requestedBloodGroup;
        this.hospitalName = hospitalName;
    }
}