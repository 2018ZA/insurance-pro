package ru.springaio.insuranceprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.springaio.insuranceprobackend.dto.ClientDto;
import ru.springaio.insuranceprobackend.entity.Client;
import ru.springaio.insuranceprobackend.entity.User;
import ru.springaio.insuranceprobackend.mapper.ClientMapper;
import ru.springaio.insuranceprobackend.repository.ClientRepository;
import ru.springaio.insuranceprobackend.repository.ClientSpecification;
import ru.springaio.insuranceprobackend.repository.UserRepository;
import ru.springaio.insuranceprobackend.security.SecurityUtils;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDto> findAll(String fullName, String passport, String phone, Pageable pageable) {
        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        
        Specification<Client> spec = Specification.where(ClientSpecification.hasFullName(fullName))
                .and(ClientSpecification.hasPassport(passport))
                .and(ClientSpecification.hasPhone(phone));

        // Ролевая модель
        String role = currentUser.getRoleCode().getCode();
        if ("AGENT".equals(role)) {
            spec = spec.and(ClientSpecification.hasAgent(currentUser));
        }

        return clientRepository.findAll(spec, pageable).map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        checkAccess(client);
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        // Проверка уникальности паспорта
        clientRepository.findByPassportSeriesAndPassportNumber(clientDto.getPassportSeries(), clientDto.getPassportNumber())
                .ifPresent(c -> { throw new RuntimeException("Client with this passport already exists"); });

        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        
        Client client = new Client();
        clientMapper.updateEntity(client, clientDto);
        client.setRegistrationDate(Instant.now());
        client.setAgent(currentUser);
        
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Override
    @Transactional
    public ClientDto update(Long id, ClientDto clientDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        checkAccess(client);
        
        clientMapper.updateEntity(client, clientDto);
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        checkAccess(client);
        
        if (!client.getContracts().isEmpty()) {
            throw new RuntimeException("Cannot delete client with active contracts");
        }
        
        clientRepository.delete(client);
    }

    private void checkAccess(Client client) {
        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        String role = currentUser.getRoleCode().getCode();
        if ("AGENT".equals(role) && !client.getAgent().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
    }
}
