package com.agustin.backend_dialysis_record.security.jwt;

import com.agustin.backend_dialysis_record.model.auth.RefreshToken;
import com.agustin.backend_dialysis_record.model.auth.UserAccount;
import com.agustin.backend_dialysis_record.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private final long refreshDays;
    private final int refreshBytes;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${security.jwt.refresh-days}") long refreshDays,
            @Value("${security.jwt.refresh-bytes}") int refreshBytes
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshDays = refreshDays;
        this.refreshBytes = refreshBytes;
    }

    public record IssuedRefreshToken(String plainToken, RefreshToken entity) {}

    public IssuedRefreshToken issue(UserAccount userAccount) {
        String plain = generateOpaqueToken();
        String hash = sha256Hex(plain);

        RefreshToken rt = new RefreshToken();
        rt.setUserAccount(userAccount);
        rt.setTokenHash(hash);
        rt.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));

        RefreshToken saved = refreshTokenRepository.save(rt);
        return new IssuedRefreshToken(plain, saved);
    }

    /**
     * Rotación:
     * - valida token actual
     * - marca lastUsedAt
     * - revoca el actual y emite uno nuevo
     */
    public IssuedRefreshToken rotate(String presentedPlainToken) {
        String hash = sha256Hex(presentedPlainToken);

        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (current.isRevoked()) throw new RuntimeException("Refresh token revoked");
        if (current.isExpired()) throw new RuntimeException("Refresh token expired");

        current.setLastUsedAt(Instant.now());

        // emitimos nuevo
        IssuedRefreshToken next = issue(current.getUserAccount());

        current.setRevokedAt(Instant.now());
        current.setReplacedByTokenId(next.entity().getId());
        refreshTokenRepository.save(current);

        return next;
    }

    @Transactional
    public void revoke(String refreshTokenPlain) {
        String hash = sha256Hex(refreshTokenPlain);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!token.isRevoked()) {
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        }
    }

    @Transactional
    public void revokeAll(UserAccount user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserAccount(user);

        Instant now = Instant.now();
        for (RefreshToken token : tokens) {
            if (!token.isRevoked()) {
                token.setRevokedAt(now);
            }
        }
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[refreshBytes];
        secureRandom.nextBytes(bytes);
        // Base64 URL-safe, sin padding (ideal para headers/body)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
