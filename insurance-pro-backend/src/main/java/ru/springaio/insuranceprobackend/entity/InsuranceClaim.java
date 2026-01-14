package ru.springaio.insuranceprobackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "insurance_claim", schema = "public", indexes = {
        @Index(name = "insurance_claim_claim_number_key", columnList = "claim_number", unique = true),
        @Index(name = "idx_claim_contract", columnList = "contract_id"),
        @Index(name = "idx_claim_status", columnList = "status_code")
})
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(name = "claim_number", nullable = false, length = 100)
    private String claimNumber;

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "claimed_amount", precision = 15, scale = 2)
    private BigDecimal claimedAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private ClaimStatus statusCode;

    @Column(name = "created_at")
    private Instant createdAt;

}