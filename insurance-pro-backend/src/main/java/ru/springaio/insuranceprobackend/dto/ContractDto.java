package ru.springaio.insuranceprobackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class ContractDto {
    private Long id;
    private String contractNumber;
    private Long clientId;
    private String clientFullName;
    private String insuranceTypeCode;
    private String insuranceTypeName;
    private Long agentId;
    private String agentFullName;
    private String statusCode;
    private String statusName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;
    private BigDecimal insuredAmount;
    private Instant createdAt;
}
