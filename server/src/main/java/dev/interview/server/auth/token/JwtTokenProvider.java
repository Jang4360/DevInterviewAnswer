package dev.interview.server.auth.token;

import dev.interview.server.auth.dto.JwtToken;
import dev.interview.server.global.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 30; // 15분
    private final long REFRESH_TOKEN_EXPIRE = 1000L * 60 * 60 * 24 * 14; // 14일

    // 토큰 생성
    public JwtToken generateTokens(UUID userId) {
        String accessToken = createToken(userId, ACCESS_TOKEN_EXPIRE);
        String refreshToken = createToken(userId, REFRESH_TOKEN_EXPIRE);

        return new JwtToken(accessToken, refreshToken);
    }

    // 토큰 생성 메서드
    private String createToken(UUID userId, Long expiry) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expiry);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    // 토큰에서 uerId 추출
    public UUID getUserId(String token) {
        try {
            return UUID.fromString(Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new UnauthorizedException("지원하지 않는 토큰 형식입니다.");
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new UnauthorizedException("토큰 형식이 잘못되었습니다.");
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new UnauthorizedException("토큰 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw new UnauthorizedException("토큰이 비어 있습니다.");
        }
    }

    public boolean isExpired(String token) {
        try {
            Date exp = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return exp.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
