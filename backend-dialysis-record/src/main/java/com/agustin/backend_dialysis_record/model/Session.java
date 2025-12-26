package com.agustin.backend_dialysis_record.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE session SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Session {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDate date;
    private LocalTime hour;
    private int bag;
    private float concentration;
    private int infusion;
    private int drainage;
    private int partial;
    private String observations;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public Session(LocalDate date, LocalTime hour, int bag, float concentration,
                   int infusion, int drainage, int partial, String observations) {
        this.date = date;
        this.hour = hour;
        this.bag = bag;
        this.concentration = concentration;
        this.infusion = infusion;
        this.drainage = drainage;
        this.partial = partial;
        this.observations = observations;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        computePartial();
    }

    private void computePartial() {
        this.partial = this.infusion - this.drainage;
    }

    @PreUpdate
    public void preUpdate() {
        computePartial();
    }

}
