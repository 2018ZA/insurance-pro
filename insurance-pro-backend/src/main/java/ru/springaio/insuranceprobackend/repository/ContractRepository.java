package ru.springaio.insuranceprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.springaio.insuranceprobackend.entity.Contract;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {
    Optional<Contract> findByContractNumber(String contractNumber);
}
