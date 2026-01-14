package ru.springaio.insuranceprobackend.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ClientDto {
    private Long id;
    private String fullName;
    private String passportSeries;
    private String passportNumber;
    private String phone;
    private String email;
    private Instant registrationDate;
    private Long agentId;
    private String agentFullName;
}
