package com.agustin.backend_dialysis_record.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE patient SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Patient {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private boolean active = true;

    private String name;
    private String surname;
    private int dni;
    private LocalDate dateOfBirth;
    private String address;
    private int number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    public Patient(String name, String surname, int dni,
                   LocalDate dateOfBirth, String address,
                   int number) {
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.number = number;
    }

    public void addSession(Session session) {
        sessions.add(session);
        session.setPatient(this);
    }

    @PrePersist
    private void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

}
