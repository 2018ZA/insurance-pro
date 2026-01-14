package ru.springaio.insuranceprobackend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import ru.springaio.insuranceprobackend.dto.ClientDto;
import ru.springaio.insuranceprobackend.service.ClientService;

// Контроллер для управления клиентами (CRUD операции)
@RestController
// Базовый путь для всех эндпоинтов клиентов
@RequestMapping("/api/clients")
// Автоматически генерирует конструктор с обязательными полями (final зависимости)
@RequiredArgsConstructor
public class ClientController {

    // Сервис для бизнес-логики работы с клиентами
    private final ClientService clientService;

    // GET запрос для получения всех клиентов с поддержкой пагинации и фильтрации
    @GetMapping
    public ResponseEntity<Page<ClientDto>> getAllClients(
            // Параметр для фильтрации по ФИО (необязательный)
            @RequestParam(required = false) String fullName,
            // Параметр для фильтрации по паспорту (необязательный)
            @RequestParam(required = false) String passport,
            // Параметр для фильтрации по телефону (необязательный)
            @RequestParam(required = false) String phone,
            // Параметр пагинации и сортировки (по умолчанию сортировка по fullName)
            @PageableDefault(sort = "fullName") Pageable pageable) {
        // Возвращает страницу с клиентами, отфильтрованную по указанным параметрам
        return ResponseEntity.ok(clientService.findAll(fullName, passport, phone, pageable));
    }

    // GET запрос для получения конкретного клиента по его ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(
            // ID клиента, передаваемый в пути URL
            @PathVariable Long id) {
        // Возвращает информацию о клиенте с указанным ID
        return ResponseEntity.ok(clientService.findById(id));
    }

    // POST запрос для создания нового клиента
    @PostMapping
    public ResponseEntity<ClientDto> createClient(
            // Данные нового клиента в формате JSON
            @RequestBody ClientDto clientDto) {
        // Создает нового клиента и возвращает его данные
        return ResponseEntity.ok(clientService.save(clientDto));
    }

    // PUT запрос для обновления существующего клиента
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(
            // ID клиента, которого нужно обновить
            @PathVariable Long id,
            // Обновленные данные клиента в формате JSON
            @RequestBody ClientDto clientDto) {
        // Обновляет клиента с указанным ID и возвращает обновленные данные
        return ResponseEntity.ok(clientService.update(id, clientDto));
    }

    // DELETE запрос для удаления клиента
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            // ID клиента, которого нужно удалить
            @PathVariable Long id) {
        // Вызывает сервис для удаления клиента
        clientService.delete(id);
        // Возвращает ответ без содержимого (статус 204 No Content)
        return ResponseEntity.noContent().build();
    }
}