package ru.springaio.insuranceprobackend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.springaio.insuranceprobackend.dto.DictionaryDto;
import ru.springaio.insuranceprobackend.repository.ContractStatusRepository;
import ru.springaio.insuranceprobackend.repository.InsuranceTypeRepository;

// Контроллер для работы со справочниками (dictionaries) системы
// Справочники содержат фиксированные данные, такие как типы страхования, статусы договоров
@RestController
// Базовый путь для всех эндпоинтов справочников
@RequestMapping("/api/dictionaries")
// Автоматически генерирует конструктор с обязательными полями (final зависимости)
@RequiredArgsConstructor
public class DictionaryController {

    // Репозиторий для работы с типами страхования в базе данных
    // Хранит справочник видов страхования (ОСАГО, КАСКО, недвижимость и т.д.)
    private final InsuranceTypeRepository insuranceTypeRepository;
    
    // Репозиторий для работы со статусами договоров в базе данных
    // Хранит справочник возможных статусов договора (активен, расторгнут, на рассмотрении и т.д.)
    private final ContractStatusRepository contractStatusRepository;

    // GET запрос для получения списка всех типов страхования
    @GetMapping("/insurance-types")
    public ResponseEntity<List<DictionaryDto>> getInsuranceTypes() {
        // Возвращает список всех типов страхования в формате DTO
        return ResponseEntity.ok(insuranceTypeRepository.findAll().stream()
                // Преобразование каждой сущности InsuranceType в DictionaryDto
                .map(t -> new DictionaryDto(t.getCode(), t.getName()))
                // Сбор всех элементов в список
                .collect(Collectors.toList()));
    }

    // GET запрос для получения списка всех статусов договоров
    @GetMapping("/contract-statuses")
    public ResponseEntity<List<DictionaryDto>> getContractStatuses() {
        // Возвращает список всех статусов договоров в формате DTO
        return ResponseEntity.ok(contractStatusRepository.findAll().stream()
                // Преобразование каждой сущности ContractStatus в DictionaryDto
                .map(s -> new DictionaryDto(s.getCode(), s.getName()))
                // Сбор всех элементов в список
                .collect(Collectors.toList()));
    }
}