package ru.springaio.insuranceprobackend.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.springaio.insuranceprobackend.dto.ContractDto;
import ru.springaio.insuranceprobackend.service.ContractService;

// Контроллер для управления договорами страхования (CRUD операции)
@RestController
// Базовый путь для всех эндпоинтов договоров
@RequestMapping("/api/contracts")
// Автоматически генерирует конструктор с обязательными полями (final зависимости)
@RequiredArgsConstructor
public class ContractController {

    // Сервис для бизнес-логики работы с договорами
    private final ContractService contractService;

    // GET запрос для получения всех договоров с поддержкой пагинации и расширенной фильтрации
    @GetMapping
    public ResponseEntity<Page<ContractDto>> getAllContracts(
            // Параметр для фильтрации по номеру договора (необязательный)
            @RequestParam(required = false) String contractNumber,
            // Параметр для фильтрации по типу страхования (необязательный)
            @RequestParam(required = false) String insuranceType,
            // Параметр для фильтрации по статусу договора (необязательный)
            @RequestParam(required = false) String status,
            // Параметр для фильтрации по начальной дате (необязательный)
            // @DateTimeFormat обеспечивает правильное преобразование строки в LocalDate
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            // Параметр для фильтрации по конечной дате (необязательный)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            // Параметр пагинации и сортировки (по умолчанию сортировка по дате создания)
            @PageableDefault(sort = "createdAt") Pageable pageable) {
        // Возвращает страницу с договорами, отфильтрованную по указанным параметрам
        return ResponseEntity.ok(contractService.findAll(contractNumber, insuranceType, status, start, end, pageable));
    }

    // GET запрос для получения конкретного договора по его ID
    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getContractById(
            // ID договора, передаваемый в пути URL
            @PathVariable Long id) {
        // Возвращает информацию о договоре с указанным ID
        return ResponseEntity.ok(contractService.findById(id));
    }

    // POST запрос для создания нового договора
    @PostMapping
    public ResponseEntity<ContractDto> createContract(
            // Данные нового договора в формате JSON
            @RequestBody ContractDto contractDto) {
        // Создает новый договор и возвращает его данные
        return ResponseEntity.ok(contractService.save(contractDto));
    }

    // PUT запрос для обновления существующего договора
    @PutMapping("/{id}")
    public ResponseEntity<ContractDto> updateContract(
            // ID договора, которого нужно обновить
            @PathVariable Long id,
            // Обновленные данные договора в формате JSON
            @RequestBody ContractDto contractDto) {
        // Обновляет договор с указанным ID и возвращает обновленные данные
        return ResponseEntity.ok(contractService.update(id, contractDto));
    }

    // DELETE запрос для удаления договора
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(
            // ID договора, которого нужно удалить
            @PathVariable Long id) {
        // Вызывает сервис для удаления договора
        contractService.delete(id);
        // Возвращает ответ без содержимого (статус 204 No Content)
        return ResponseEntity.noContent().build();
    }
}