package ru.springaio.insuranceprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.springaio.insuranceprobackend.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
}
