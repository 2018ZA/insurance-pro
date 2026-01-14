package ru.springaio.insuranceprobackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.springaio.insuranceprobackend.dto.ClientDto;

public interface ClientService {
    Page<ClientDto> findAll(String fullName, String passport, String phone, Pageable pageable);
    ClientDto findById(Long id);
    ClientDto save(ClientDto clientDto);
    ClientDto update(Long id, ClientDto clientDto);
    void delete(Long id);
}
