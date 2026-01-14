package ru.springaio.insuranceprobackend.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.springaio.insuranceprobackend.entity.Contract;
import ru.springaio.insuranceprobackend.entity.User;

import java.time.LocalDate;

public class ContractSpecification {

    public static Specification<Contract> hasContractNumber(String contractNumber) {
        return (root, query, cb) -> contractNumber == null || contractNumber.isEmpty() ?
                null : cb.like(cb.lower(root.get("contractNumber")), "%" + contractNumber.toLowerCase() + "%");
    }

    public static Specification<Contract> hasInsuranceType(String typeCode) {
        return (root, query, cb) -> typeCode == null || typeCode.isEmpty() ?
                null : cb.equal(root.get("insuranceTypeCode").get("code"), typeCode);
    }

    public static Specification<Contract> hasStatus(String statusCode) {
        return (root, query, cb) -> statusCode == null || statusCode.isEmpty() ?
                null : cb.equal(root.get("statusCode").get("code"), statusCode);
    }

    public static Specification<Contract> hasAgent(User agent) {
        return (root, query, cb) -> agent == null ? null : cb.equal(root.get("agent"), agent);
    }

    public static Specification<Contract> isWithinPeriod(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("startDate"), start),
                        cb.lessThanOrEqualTo(root.get("endDate"), end)
                );
            }
            if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("startDate"), start);
            }
            return cb.lessThanOrEqualTo(root.get("endDate"), end);
        };
    }
}
