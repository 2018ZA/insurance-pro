package ru.springaio.insuranceprobackend.mapper;

import org.springframework.stereotype.Component;
import ru.springaio.insuranceprobackend.dto.ClientDto;
import ru.springaio.insuranceprobackend.entity.Client;

@Component
public class ClientMapper {

    public ClientDto toDto(Client client) {
        if (client == null) return null;
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setFullName(client.getFullName());
        dto.setPassportSeries(client.getPassportSeries());
        dto.setPassportNumber(client.getPassportNumber());
        dto.setPhone(client.getPhone());
        dto.setEmail(client.getEmail());
        dto.setRegistrationDate(client.getRegistrationDate());
        if (client.getAgent() != null) {
            dto.setAgentId(client.getAgent().getId());
            dto.setAgentFullName(client.getAgent().getFullName());
        }
        return dto;
    }

    public void updateEntity(Client client, ClientDto dto) {
        if (dto == null) return;
        client.setFullName(dto.getFullName());
        client.setPassportSeries(dto.getPassportSeries());
        client.setPassportNumber(dto.getPassportNumber());
        client.setPhone(dto.getPhone());
        client.setEmail(dto.getEmail());
    }
}
