package ru.springaio.insuranceprobackend.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.springaio.insuranceprobackend.entity.Client;
import ru.springaio.insuranceprobackend.entity.User;

public class ClientSpecification {

    public static Specification<Client> hasFullName(String fullName) {
        return (root, query, cb) -> fullName == null || fullName.isEmpty() ?
                null : cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<Client> hasPassport(String passport) {
        return (root, query, cb) -> {
            if (passport == null || passport.isEmpty()) return null;
            // Точное совпадение по серии и номеру (если передано вместе) или по чему-то одному
            // В требованиях: "Поиск клиентов по паспортным данным (точное совпадение)"
            // Предположим, ищем по номеру
            return cb.equal(root.get("passportNumber"), passport);
        };
    }

    public static Specification<Client> hasPhone(String phone) {
        return (root, query, cb) -> phone == null || phone.isEmpty() ?
                null : cb.like(root.get("phone"), "%" + phone + "%");
    }

    public static Specification<Client> hasAgent(User agent) {
        return (root, query, cb) -> agent == null ? null : cb.equal(root.get("agent"), agent);
    }
}
