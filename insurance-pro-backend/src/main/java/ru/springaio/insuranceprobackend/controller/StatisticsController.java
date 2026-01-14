package ru.springaio.insuranceprobackend.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.springaio.insuranceprobackend.dto.StatisticsDto;
import ru.springaio.insuranceprobackend.service.StatisticsService;

// Контроллер для работы со статистикой системы страхования
// Предоставляет аналитические данные и сводки по клиентам, договорам и премиям
@RestController
// Базовый путь для всех эндпоинтов статистики
@RequestMapping("/api/statistics")
// Автоматически генерирует конструктор с обязательными полями (final зависимости)
@RequiredArgsConstructor
public class StatisticsController {

    // Сервис для бизнес-логики работы со статистикой
    // Вычисляет различные метрики и агрегированные данные
    private final StatisticsService statisticsService;

    // GET запрос для получения статистических данных за указанный период
    @GetMapping
    public ResponseEntity<StatisticsDto> getStatistics(
            // Начальная дата периода для фильтрации статистики (необязательный параметр)
            // @DateTimeFormat обеспечивает автоматическое преобразование строки формата ISO (YYYY-MM-DD) в LocalDate
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            // Конечная дата периода для фильтрации статистики (необязательный параметр)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // Возвращает объект StatisticsDto с агрегированными статистическими данными
        // Если даты не указаны, возвращает статистику за весь период
        return ResponseEntity.ok(statisticsService.getStatistics(startDate, endDate));
    }
}