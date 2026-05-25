package com.huseyin.personalfinanceapi.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenJpa, Long> {

    Optional<RefreshTokenJpa> findByTokenHash(String tokenHash);

    Optional<RefreshTokenJpa> findByUserIdAndTokenHash(Long userId,String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenJpa r SET r.revoked = true WHERE r.userId = :userId AND r.revoked = false")
    int revokeAllForUser(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpa r WHERE r.expiresAt < :now OR r.revoked = true")
    int deleteExpiredOrRevoked(@Param("now") Instant now);

}
