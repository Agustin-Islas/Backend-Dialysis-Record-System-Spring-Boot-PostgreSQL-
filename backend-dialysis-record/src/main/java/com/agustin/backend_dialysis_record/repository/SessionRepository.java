package com.agustin.backend_dialysis_record.repository;

import com.agustin.backend_dialysis_record.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByPatientIdOrderByDateDesc(Long patientId);

}
