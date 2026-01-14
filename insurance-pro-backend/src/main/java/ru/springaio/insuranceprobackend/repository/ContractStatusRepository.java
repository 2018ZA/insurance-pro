package ru.springaio.insuranceprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.springaio.insuranceprobackend.entity.ContractStatus;

@Repository
public interface ContractStatusRepository extends JpaRepository<ContractStatus, String> {
}
