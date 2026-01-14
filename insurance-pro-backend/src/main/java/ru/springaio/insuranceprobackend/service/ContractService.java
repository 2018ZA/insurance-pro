package ru.springaio.insuranceprobackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.springaio.insuranceprobackend.dto.ContractDto;

import java.time.LocalDate;

public interface ContractService {
    Page<ContractDto> findAll(String contractNumber, String insuranceType, String status, LocalDate start, LocalDate end, Pageable pageable);
    ContractDto findById(Long id);
    ContractDto save(ContractDto contractDto);
    ContractDto update(Long id, ContractDto contractDto);
    void delete(Long id);
}
