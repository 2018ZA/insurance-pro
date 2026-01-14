package ru.springaio.insuranceprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.springaio.insuranceprobackend.dto.ContractDto;
import ru.springaio.insuranceprobackend.entity.Contract;
import ru.springaio.insuranceprobackend.entity.User;
import ru.springaio.insuranceprobackend.mapper.ContractMapper;
import ru.springaio.insuranceprobackend.repository.ContractRepository;
import ru.springaio.insuranceprobackend.repository.ContractSpecification;
import ru.springaio.insuranceprobackend.repository.UserRepository;
import ru.springaio.insuranceprobackend.security.SecurityUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final ContractMapper contractMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDto> findAll(String contractNumber, String insuranceType, String status, LocalDate start, LocalDate end, Pageable pageable) {
        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        
        Specification<Contract> spec = Specification.where(ContractSpecification.hasContractNumber(contractNumber))
                .and(ContractSpecification.hasInsuranceType(insuranceType))
                .and(ContractSpecification.hasStatus(status))
                .and(ContractSpecification.isWithinPeriod(start, end));

        // Ролевая модель
        String role = currentUser.getRoleCode().getCode();
        if ("AGENT".equals(role)) {
            spec = spec.and(ContractSpecification.hasAgent(currentUser));
        }

        return contractRepository.findAll(spec, pageable).map(contractMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractDto findById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        checkAccess(contract);
        return contractMapper.toDto(contract);
    }

    @Override
    @Transactional
    public ContractDto save(ContractDto contractDto) {
        if (contractDto.getEndDate().isBefore(contractDto.getStartDate()) || contractDto.getEndDate().isEqual(contractDto.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        
        Contract contract = new Contract();
        contractMapper.updateEntity(contract, contractDto);
        contract.setCreatedAt(Instant.now());
        contract.setAgent(currentUser);
        contract.setContractNumber("INS-" + System.currentTimeMillis() + "-" + (100 + new Random().nextInt(900)));
        
        return contractMapper.toDto(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ContractDto update(Long id, ContractDto contractDto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        checkAccess(contract);

        if (contractDto.getEndDate().isBefore(contractDto.getStartDate()) || contractDto.getEndDate().isEqual(contractDto.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        
        contractMapper.updateEntity(contract, contractDto);
        return contractMapper.toDto(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        checkAccess(contract);
        contractRepository.delete(contract);
    }

    private void checkAccess(Contract contract) {
        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        String role = currentUser.getRoleCode().getCode();
        // Агент может редактировать только свои договоры. Менеджер и Админ — любые.
        if ("AGENT".equals(role) && !contract.getAgent().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
    }
}
