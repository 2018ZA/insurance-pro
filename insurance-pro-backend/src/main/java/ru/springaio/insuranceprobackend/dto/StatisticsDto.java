package ru.springaio.insuranceprobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class StatisticsDto {
    private long totalClients;
    private long totalContracts;
    private List<TypeCountDto> contractsByType;
    private List<TypeAverageDto> averagePremiumByType;
    private List<MonthCountDto> dynamicByMonth;
}
