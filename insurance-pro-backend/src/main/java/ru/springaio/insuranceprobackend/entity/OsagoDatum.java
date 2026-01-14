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

@Getter
@Setter
@Entity
@Table(name = "osago_data", schema = "public", indexes = {
        @Index(name = "osago_data_contract_id_key", columnList = "contract_id", unique = true)
})
public class OsagoDatum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Column(name = "vehicle_model", nullable = false, length = 100)
    private String vehicleModel;

    @Column(name = "vin", length = 50)
    private String vin;

    @Column(name = "driving_experience")
    private Integer drivingExperience;

}