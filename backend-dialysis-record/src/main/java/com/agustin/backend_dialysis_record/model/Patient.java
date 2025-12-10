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

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE patient SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean active = true;

    private String name;
    private String surname;
    private int dni;
    private LocalDate dateOfBirth;
    private String address;
    private int number;
    private String email;
    @ManyToOne
    private Doctor doctor;
    @OneToMany(mappedBy = "patient")
    private List<Session> sessions = new ArrayList<Session>();

    public Patient(String name, String surname, int dni,
                   LocalDate dateOfBirth, String address,
                   int number, String email) {
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.number = number;
        this.email = email;
    }

    public void addSession(Session session) {
        sessions.add(session);
        session.setPatient(this);
    }
}
