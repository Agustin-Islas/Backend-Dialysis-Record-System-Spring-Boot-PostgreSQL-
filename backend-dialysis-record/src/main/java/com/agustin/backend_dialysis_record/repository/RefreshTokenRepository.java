package com.agustin.backend_dialysis_record.repository;

import com.agustin.backend_dialysis_record.model.auth.RefreshToken;
import com.agustin.backend_dialysis_record.model.auth.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUserAccount(UserAccount userAccount);
    long deleteByUserAccount(UserAccount userAccount);
}
