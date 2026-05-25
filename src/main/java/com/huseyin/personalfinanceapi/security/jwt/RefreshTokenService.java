package com.huseyin.personalfinanceapi.security.jwt;

import com.huseyin.personalfinanceapi.security.exception.RefreshTokenAlreadyRevoked;
import com.huseyin.personalfinanceapi.security.exception.RefreshTokenExpiredException;
import com.huseyin.personalfinanceapi.security.exception.RefreshTokenNotExistsException;
import com.huseyin.personalfinanceapi.security.exception.ReuseRevokedTokenException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;


@Service
public class RefreshTokenService {

    /** Refresh-token lifetime in milliseconds (default: 30 days). */
    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationTime;

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository
    ){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public IssuedToken issue(Long userId) throws NoSuchAlgorithmException {
        // create refresh jwt from Jwt service
        String refreshRaw = UUID.randomUUID().toString();

        //Take ınstant utc time as now
        Instant now = Instant.now();

        RefreshTokenJpa refreshTokenJpa = new RefreshTokenJpa();
        refreshTokenJpa.setUserId(userId);
        refreshTokenJpa.setTokenHash(hash(refreshRaw));
        refreshTokenJpa.setIssuedAt(now);
        refreshTokenJpa.setExpiresAt(now.plusMillis(refreshExpirationTime));
        refreshTokenJpa.setRevoked(false);
        RefreshTokenJpa saved = refreshTokenRepository.save(refreshTokenJpa);

        return new IssuedToken(refreshRaw,saved.getId(),refreshExpirationTime / 1000);

    }

    /**
     * This method is called for refreshing an existing non-revoked refresh token
     * by making it revoked and generates a new refresh token and save it to the DB
     *
     * If the caller presents a token whose row is already revoked, this
     *  is treated as a REUSE attack: every outstanding refresh token for
     *  the user is revoked as a defensive measure.
     *
     * @param rawRefreshToken
     * @return RotationResult
     */
    @Transactional
    public RotationResult rotate(String rawRefreshToken) throws NoSuchAlgorithmException {

        String hashedToken = hash(rawRefreshToken);

        RefreshTokenJpa refreshTokenJpa = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new RefreshTokenNotExistsException(rawRefreshToken+ "does not exist" ));

        if(refreshTokenJpa.isRevoked()){
            revokeAllForUser(refreshTokenJpa.getUserId());
            return new RotationResult(refreshTokenJpa.getUserId(),null,new ReuseRevokedTokenException("Token already has been revoked. All your tokens have been invalidated"));
        }
        if(refreshTokenJpa.getExpiresAt().isBefore(Instant.now())){
            refreshTokenJpa.setRevoked(true);
            refreshTokenRepository.save(refreshTokenJpa);
            return new RotationResult(refreshTokenJpa.getUserId(),
                    null,
                    new RefreshTokenExpiredException("Refresh token cannot be used due to expiration"));
        }

        // revoking old token
        refreshTokenJpa.setRevoked(true);
        refreshTokenRepository.save(refreshTokenJpa);

        // issue a new refresh token
        IssuedToken issuedToken = issue(refreshTokenJpa.getUserId());

        return new RotationResult(refreshTokenJpa.getUserId(),issuedToken,null);

    }

    // Revoke will be used when user tries to Log-out
    @Transactional
    public void revoke(Long userId,String rawRefreshToken) throws NoSuchAlgorithmException {
        RefreshTokenJpa refreshTokenJpa = refreshTokenRepository.findByUserIdAndTokenHash(userId,hash(rawRefreshToken))
                .orElseThrow(() -> new RefreshTokenNotExistsException("Refresh token for user:"+userId+" not found"));

        if(refreshTokenJpa.isRevoked()){
            throw new RefreshTokenAlreadyRevoked("Refresh token is already revoked");
        }
        refreshTokenJpa.setRevoked(true);
        refreshTokenRepository.save(refreshTokenJpa);
    }


    public void revokeAllForUser(Long userId){
        refreshTokenRepository.revokeAllForUser(userId);
    }

    //-----------------Helpers----------

    /** SHA-256 hex digest. */
    static String hash(String rawToken) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hashBytes);
    }

    public record IssuedToken(String token, Long id, long expiresIn) {}
    public record RotationResult(Long userId, IssuedToken issuedToken,RuntimeException exception) {}

}
