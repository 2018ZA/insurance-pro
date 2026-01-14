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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "contract", schema = "public", indexes = {
        @Index(name = "contract_contract_number_key", columnList = "contract_number", unique = true),
        @Index(name = "idx_contract_client", columnList = "client_id"),
        @Index(name = "idx_contract_type", columnList = "insurance_type_code"),
        @Index(name = "idx_contract_agent", columnList = "agent_id"),
        @Index(name = "idx_contract_status", columnList = "status_code")
})
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "contract_number", nullable = false, length = 100)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_type_code")
    private InsuranceType insuranceTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")
    private ContractStatus statusCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "premium_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumAmount;

    @Column(name = "insured_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredAmount;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToOne(mappedBy = "contract")
    private CascoDatum cascoDatum;

    @OneToMany(mappedBy = "contract")
    private Set<InsuranceClaim> insuranceClaims = new LinkedHashSet<>();

    @OneToOne(mappedBy = "contract")
    private LifeInsuranceDatum lifeInsuranceDatum;

    @OneToOne(mappedBy = "contract")
    private OsagoDatum osagoDatum;

    @OneToMany(mappedBy = "contract")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToOne(mappedBy = "contract")
    private PropertyInsuranceDatum propertyInsuranceDatum;

}