package ru.springaio.insuranceprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.springaio.insuranceprobackend.entity.InsuranceType;

@Repository
public interface InsuranceTypeRepository extends JpaRepository<InsuranceType, String> {
}
