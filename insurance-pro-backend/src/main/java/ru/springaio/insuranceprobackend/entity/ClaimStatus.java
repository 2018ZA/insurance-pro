package ru.springaio.insuranceprobackend.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Аннотация Lombok для автоматической генерации геттеров и сеттеров
@Getter
@Setter
// Указывает, что этот класс является сущностью JPA
@Entity
// Определяет таблицу в базе данных, с которой связана эта сущность
@Table(name = "claim_status", // Имя таблицы в базе данных
       schema = "public")      // Схема базы данных
public class ClaimStatus {
    
    // Первичный ключ сущности - строковый код статуса
    @Id
    // Определяет колонку в таблице
    @Column(name = "code",        // Имя колонки
            nullable = false,     // NOT NULL ограничение
            length = 50)          // Максимальная длина строки
    private String code;          // Код статуса страхового случая (например: "new", "in_progress", "closed")

    // Название статуса для отображения
    @Column(name = "name",        // Имя колонки
            nullable = false,     // NOT NULL ограничение
            length = 100)         // Максимальная длина строки
    private String name;          // Человекочитаемое название статуса (например: "Новый", "В обработке", "Закрыт")

    // Обратная связь один-ко-многим с сущностью InsuranceClaim
    // mappedBy указывает на поле "statusCode" в классе InsuranceClaim, которое управляет связью
    @OneToMany(mappedBy = "statusCode")
    // LinkedHashSet сохраняет порядок добавления элементов
    private Set<InsuranceClaim> insuranceClaims = new LinkedHashSet<>();
    // Набор страховых случаев, которые имеют данный статус
}