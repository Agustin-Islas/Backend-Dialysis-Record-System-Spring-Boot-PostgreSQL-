package com.agustin.backend_dialysis_record.repository;

import com.agustin.backend_dialysis_record.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    List<Session> findByPatientIdOrderByDateDesc(UUID patientId);

    List<Session> findByPatientIdOrderByDateDescHourDesc(UUID patientId);

    List<Session> findByPatientIdAndDateOrderByHourDesc(UUID patientId, LocalDate date);

    List<Session> findByPatientIdAndDateBetweenOrderByDateDescHourDesc(UUID patientId, LocalDate startDate, LocalDate endDate);

    @Query("select s.patient.id from Session s where s.id = :sessionId")
    Optional<UUID> findPatientIdBySessionId(UUID sessionId);

}
