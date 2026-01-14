package ru.springaio.insuranceprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.springaio.insuranceprobackend.dto.StatisticsDto;
import ru.springaio.insuranceprobackend.dto.TypeCountDto;
import ru.springaio.insuranceprobackend.dto.TypeAverageDto;
import ru.springaio.insuranceprobackend.dto.MonthCountDto;
import ru.springaio.insuranceprobackend.entity.User;
import ru.springaio.insuranceprobackend.repository.ClientRepository;
import ru.springaio.insuranceprobackend.repository.ContractRepository;
import ru.springaio.insuranceprobackend.repository.UserRepository;
import ru.springaio.insuranceprobackend.security.SecurityUtils;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public StatisticsDto getStatistics(LocalDate startDate, LocalDate endDate) {
        User currentUser = userRepository.findByLogin(securityUtils.getCurrentUserLogin()).orElseThrow();
        String role = currentUser.getRoleCode().getCode();
        Long agentId = "AGENT".equals(role) ? currentUser.getId() : null;

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        long totalClients;
        long totalContracts;

        if (agentId != null) {
            totalClients = clientRepository.count((root, query, cb) -> {
                var predicate = cb.equal(root.get("agent").get("id"), agentId);
                if (start != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("registrationDate"), start));
                if (end != null) predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("registrationDate"), end));
                return predicate;
            });
            totalContracts = contractRepository.count((root, query, cb) -> {
                var predicate = cb.equal(root.get("agent").get("id"), agentId);
                if (start != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), start));
                if (end != null) predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), end));
                return predicate;
            });
        } else {
            totalClients = clientRepository.count((root, query, cb) -> {
                var predicate = cb.conjunction();
                if (start != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("registrationDate"), start));
                if (end != null) predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("registrationDate"), end));
                return predicate;
            });
            totalContracts = contractRepository.count((root, query, cb) -> {
                var predicate = cb.conjunction();
                if (start != null) predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), start));
                if (end != null) predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), end));
                return predicate;
            });
        }

        List<TypeCountDto> contractsByType = getContractsByType(agentId, start, end);
        List<TypeAverageDto> averagePremiumByType = getAveragePremiumByType(agentId, start, end);
        List<MonthCountDto> dynamicByMonth = getDynamicByMonth(agentId, start, end);

        return StatisticsDto.builder()
                .totalClients(totalClients)
                .totalContracts(totalContracts)
                .contractsByType(contractsByType)
                .averagePremiumByType(averagePremiumByType)
                .dynamicByMonth(dynamicByMonth)
                .build();
    }

    private List<TypeCountDto> getContractsByType(Long agentId, LocalDateTime start, LocalDateTime end) {
        StringBuilder queryStr = new StringBuilder("SELECT new ru.springaio.insuranceprobackend.dto.TypeCountDto(c.insuranceTypeCode.name, COUNT(c)) FROM Contract c WHERE 1=1 ");
        if (agentId != null) queryStr.append("AND c.agent.id = :agentId ");
        if (start != null) queryStr.append("AND c.createdAt >= :start ");
        if (end != null) queryStr.append("AND c.createdAt <= :end ");
        queryStr.append("GROUP BY c.insuranceTypeCode.name");

        var query = entityManager.createQuery(queryStr.toString(), TypeCountDto.class);
        if (agentId != null) query.setParameter("agentId", agentId);
        if (start != null) query.setParameter("start", start);
        if (end != null) query.setParameter("end", end);
        return query.getResultList();
    }

    private List<TypeAverageDto> getAveragePremiumByType(Long agentId, LocalDateTime start, LocalDateTime end) {
        StringBuilder queryStr = new StringBuilder("SELECT c.insuranceTypeCode.name, AVG(c.premiumAmount) FROM Contract c WHERE 1=1 ");
        if (agentId != null) queryStr.append("AND c.agent.id = :agentId ");
        if (start != null) queryStr.append("AND c.createdAt >= :start ");
        if (end != null) queryStr.append("AND c.createdAt <= :end ");
        queryStr.append("GROUP BY c.insuranceTypeCode.name");

        var query = entityManager.createQuery(queryStr.toString());
        if (agentId != null) query.setParameter("agentId", agentId);
        if (start != null) query.setParameter("start", start);
        if (end != null) query.setParameter("end", end);
        
        List<Object[]> results = query.getResultList();
        return results.stream()
                .map(r -> new TypeAverageDto((String) r[0], r[1] != null ? BigDecimal.valueOf(((Number) r[1]).doubleValue()) : BigDecimal.ZERO))
                .collect(Collectors.toList());
    }

    private List<MonthCountDto> getDynamicByMonth(Long agentId, LocalDateTime start, LocalDateTime end) {
        StringBuilder queryStr = new StringBuilder("SELECT TO_CHAR(c.created_at, 'YYYY-MM'), COUNT(c) FROM contract c WHERE 1=1 ");
        if (agentId != null) queryStr.append("AND c.agent_id = :agentId ");
        if (start != null) queryStr.append("AND c.created_at >= :start ");
        if (end != null) queryStr.append("AND c.created_at <= :end ");
        queryStr.append("GROUP BY TO_CHAR(c.created_at, 'YYYY-MM') ORDER BY TO_CHAR(c.created_at, 'YYYY-MM')");

        var query = entityManager.createNativeQuery(queryStr.toString());
        if (agentId != null) query.setParameter("agentId", agentId);
        if (start != null) query.setParameter("start", start);
        if (end != null) query.setParameter("end", end);

        List<Object[]> results = query.getResultList();
        return results.stream()
                .map(r -> new MonthCountDto((String) r[0], ((Number) r[1]).longValue()))
                .collect(Collectors.toList());
    }
}
