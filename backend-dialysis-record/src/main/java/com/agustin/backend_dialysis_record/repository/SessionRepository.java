package com.agustin.backend_dialysis_record.repository;

import com.agustin.backend_dialysis_record.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByPatientIdOrderByDateDesc(UUID patientId);

}
