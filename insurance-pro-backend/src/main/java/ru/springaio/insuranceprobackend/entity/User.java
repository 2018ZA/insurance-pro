package ru.springaio.insuranceprobackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "public", indexes = {
        @Index(name = "users_login_key", columnList = "login", unique = true),
        @Index(name = "idx_users_role", columnList = "role_code")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", nullable = false, length = 100)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code")
    private UserRole roleCode;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "agent")
    private Set<Contract> contracts = new LinkedHashSet<>();

}