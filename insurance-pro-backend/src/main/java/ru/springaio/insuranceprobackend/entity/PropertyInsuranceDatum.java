package ru.springaio.insuranceprobackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "property_insurance_data", schema = "public", indexes = {
        @Index(name = "property_insurance_data_contract_id_key", columnList = "contract_id", unique = true)
})
public class PropertyInsuranceDatum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(name = "property_type", nullable = false, length = 100)
    private String propertyType;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "construction_year")
    private Integer constructionYear;

    @Column(name = "cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal cost;

}