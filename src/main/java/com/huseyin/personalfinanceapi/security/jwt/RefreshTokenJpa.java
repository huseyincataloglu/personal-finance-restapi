package com.huseyin.personalfinanceapi.security.jwt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


/**
 * persisting hashed refresh token a long with some attributes in a table
 *
 * We never store raw refresh never. We hash it, sha-256 hash, it means
 * a database exposure would not leak valid refresh token and our server can validate
 * it by hashing raw refresh and comparing persisted one.
 *
 * Each hashed jwt will be stored in a row. revoking means disabling existing one
 * and producing a new one instead of it. isRevoke will also be set true when
 * we rotate refrest tokens
 *
 */
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_hash", columnList = "token_hash", unique = true),
                @Index(name = "idx_refresh_token_user", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** SHA-256 hex digest of the raw refresh JWT. Never the raw token. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    /** Set true on logout, rotation, or admin revocation. */
    @Column(nullable = false)
    private boolean revoked = false;

}
