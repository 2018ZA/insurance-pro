package ru.springaio.insuranceprobackend.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Аннотация Lombok для автоматической генерации геттеров и сеттеров
@Getter
@Setter
// Указывает, что этот класс является сущностью JPA
@Entity
// Определяет таблицу в базе данных, с которой связана эта сущность
@Table(name = "casco_data", // Имя таблицы в базе данных
       schema = "public",   // Схема базы данных
       indexes = {
           // Создает уникальный индекс на поле contract_id для обеспечения уникальности
           @Index(name = "casco_data_contract_id_key", columnList = "contract_id", unique = true)
       })
public class CascoDatum {
    
    // Первичный ключ сущности
    @Id
    // Стратегия генерации ID - автоинкремент в базе данных
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Определяет колонку в таблице, nullable = false означает NOT NULL ограничение
    @Column(name = "id", nullable = false)
    private Long id;

    // Связь один-к-одному с сущностью Contract
    @OneToOne(fetch = FetchType.LAZY) // LAZY загрузка для оптимизации производительности
    // Каскадное удаление: при удалении контракта удаляются связанные данные КАСКО
    @OnDelete(action = OnDeleteAction.CASCADE)
    // Внешний ключ для связи с таблицей contracts
    @JoinColumn(name = "contract_id")
    private Contract contract;

    // Модель транспортного средства
    @Column(name = "vehicle_model", nullable = false, length = 100)
    private String vehicleModel;

    // Год выпуска транспортного средства
    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    // Стоимость транспортного средства
    // precision определяет общее количество цифр, scale - количество знаков после запятой
    @Column(name = "vehicle_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal vehicleCost;

    // Наличие франшизы (дополнительное условие страхования)
    @Column(name = "has_franchise")
    private Boolean hasFranchise;

    // Размер франшизы (если она есть)
    @Column(name = "franchise_amount", precision = 15, scale = 2)
    private BigDecimal franchiseAmount;
}