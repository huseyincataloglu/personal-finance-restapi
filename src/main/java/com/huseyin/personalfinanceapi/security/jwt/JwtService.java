package com.huseyin.personalfinanceapi.security.jwt;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expirationTime;

    public IssuedAccessToken createAccessToken(Long id, String email, List<String> roles){

        Instant now = Instant.now();

        String accessToken = Jwts.builder()
                .subject(id.toString()) // refers the owner of the token. It is user id
                .claim("email",email)
                .claim("roles",roles)
                .expiration(Date.from(now.plusMillis(expirationTime)))
                .issuedAt(Date.from(now))
                .signWith(getAccessSigningKey(),Jwts.SIG.HS256)
                .compact();

        return new IssuedAccessToken(accessToken,expirationTime/1000);

    }

    private SecretKey getAccessSigningKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
    public String validateAndExtractId(String token) {
        return extractClaim(token, Claims::getSubject,getAccessSigningKey());
    }
    public Object extractRoles(String token){
        return extractAllClaims(token,getAccessSigningKey()).get("roles", List.class);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration,getAccessSigningKey());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver,SecretKey key) {
        final Claims claims = extractAllClaims(token,key);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token,SecretKey key){
        JwtParser parser = Jwts.parser().verifyWith(key).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public record IssuedAccessToken(String accessToken, Long expiresIn) {}

}
