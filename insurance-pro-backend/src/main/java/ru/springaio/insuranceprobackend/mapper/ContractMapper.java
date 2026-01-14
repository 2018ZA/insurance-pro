package ru.springaio.insuranceprobackend.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.springaio.insuranceprobackend.dto.ContractDto;
import ru.springaio.insuranceprobackend.entity.Contract;
import ru.springaio.insuranceprobackend.repository.ClientRepository;
import ru.springaio.insuranceprobackend.repository.ContractStatusRepository;
import ru.springaio.insuranceprobackend.repository.InsuranceTypeRepository;

@Component
@RequiredArgsConstructor
public class ContractMapper {

    private final ClientRepository clientRepository;
    private final InsuranceTypeRepository insuranceTypeRepository;
    private final ContractStatusRepository contractStatusRepository;

    public ContractDto toDto(Contract contract) {
        if (contract == null) return null;
        ContractDto dto = new ContractDto();
        dto.setId(contract.getId());
        dto.setContractNumber(contract.getContractNumber());
        if (contract.getClient() != null) {
            dto.setClientId(contract.getClient().getId());
            dto.setClientFullName(contract.getClient().getFullName());
        }
        if (contract.getInsuranceTypeCode() != null) {
            dto.setInsuranceTypeCode(contract.getInsuranceTypeCode().getCode());
            dto.setInsuranceTypeName(contract.getInsuranceTypeCode().getName());
        }
        if (contract.getAgent() != null) {
            dto.setAgentId(contract.getAgent().getId());
            dto.setAgentFullName(contract.getAgent().getFullName());
        }
        if (contract.getStatusCode() != null) {
            dto.setStatusCode(contract.getStatusCode().getCode());
            dto.setStatusName(contract.getStatusCode().getName());
        }
        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setPremiumAmount(contract.getPremiumAmount());
        dto.setInsuredAmount(contract.getInsuredAmount());
        dto.setCreatedAt(contract.getCreatedAt());
        return dto;
    }

    public void updateEntity(Contract contract, ContractDto dto) {
        if (dto == null) return;
        contract.setStartDate(dto.getStartDate());
        contract.setEndDate(dto.getEndDate());
        contract.setPremiumAmount(dto.getPremiumAmount());
        contract.setInsuredAmount(dto.getInsuredAmount());
        
        if (dto.getClientId() != null) {
            contract.setClient(clientRepository.findById(dto.getClientId()).orElse(null));
        }
        if (dto.getInsuranceTypeCode() != null) {
            contract.setInsuranceTypeCode(insuranceTypeRepository.findById(dto.getInsuranceTypeCode()).orElse(null));
        }
        if (dto.getStatusCode() != null) {
            contract.setStatusCode(contractStatusRepository.findById(dto.getStatusCode()).orElse(null));
        }
    }
}
