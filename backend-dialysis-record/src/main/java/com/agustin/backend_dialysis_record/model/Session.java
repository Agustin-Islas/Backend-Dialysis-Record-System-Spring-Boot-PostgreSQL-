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
    public static final LocalTime CLINICAL_CUTOFF = LocalTime.of(5, 0);

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDate date;
    private LocalTime hour;
    
    @Column(name = "clinical_date")
    private LocalDate clinicalDate;

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
                   int infusion, int drainage, String observations) {
        this.date = date;
        this.hour = hour;
        this.bag = bag;
        this.concentration = concentration;
        this.infusion = infusion;
        this.drainage = drainage;
        this.observations = observations;
    }

    public LocalDate getClinicalDate() {
        if (clinicalDate == null && date != null) {
            computeClinicalDate();
        }
        return clinicalDate != null ? clinicalDate : date;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        computePartial();
        computeClinicalDate();
    }

    private void computePartial() {
        this.partial = this.infusion - this.drainage;
    }

    public void computeClinicalDate() {
        if (this.date != null && this.hour != null && this.hour.isBefore(CLINICAL_CUTOFF)) {
            this.clinicalDate = this.date.minusDays(1);
        } else if (this.date != null) {
            this.clinicalDate = this.date;
        }
    }

    @PreUpdate
    public void preUpdate() {
        computePartial();
        computeClinicalDate();
    }

}
