package com.agustin.backend_dialysis_record.repository;

import com.agustin.backend_dialysis_record.model.auth.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndPatient_Id(UUID userAccountId, UUID patientId);

    boolean existsByIdAndDoctor_Id(UUID userAccountId, UUID doctorId);

    @Query("select ua.doctor.id from UserAccount ua where ua.id = :userAccountId")
    Optional<UUID> findDoctorIdByUserAccountId(UUID userAccountId);

    @Query("select ua.patient.id from UserAccount ua where ua.id = :userAccountId")
    Optional<UUID> findPatientIdByUserAccountId(UUID userAccountId);

}
