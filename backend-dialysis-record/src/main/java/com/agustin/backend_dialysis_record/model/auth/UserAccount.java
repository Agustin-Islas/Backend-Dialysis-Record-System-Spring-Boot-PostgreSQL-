package com.agustin.backend_dialysis_record.model.auth;

import com.agustin.backend_dialysis_record.model.Doctor;
import com.agustin.backend_dialysis_record.model.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "user_account",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_account_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    @OneToOne
    private Doctor doctor;

    @OneToOne
    private Patient patient;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
