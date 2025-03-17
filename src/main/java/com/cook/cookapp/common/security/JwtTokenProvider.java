package com.cook.cookapp.common.security;


import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String stringSecretKey;  // String 형식보다는 Key 형식이 안전

    private Key secretKey;

    // 블랙리스트 (로그아웃된 토큰 저장) - 간단한 구현 (Redis 사용 가능)
    private final Set<String> invalidatedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 저장된 리프레시 토큰 목록 (실제 환경에서는 Redis 사용 권장)
    private final Map<Long, String> refreshTokenStore = new HashMap<>();

    // 토큰의 유효시간
//    public static final long TOKEN_VALID_TIME = 1000L * 60 * 5; // Access 토큰 5분(밀리초)
    public static final long TOKEN_VALID_TIME = 1000L * 60 * 60;    // Access 토큰 1시간(밀리초) - 임시
    public static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7;   // 일주일(밀리초)
    public static final int REFRESH_TOKEN_VALID_TIME_IN_COOKIE = 60 * 60 * 24 * 7; // 일주일(초)
    public static final int REFRESH_TOKEN_VALID_TIME_IN_REDIS = 60 * 60 * 24 * 7; // 일주일(초)

    private final CustomUserDetailsService userDetailService;

    // 객체 초기화, secretKey 를 Base64 로 인코딩 후 Key 객체로 변환
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(stringSecretKey.getBytes(StandardCharsets.UTF_8));
        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Jwt AccessToken 생성
    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .setHeaderParam("type", "accessToken")
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Access 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALID_TIME)) // Access 토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Jwt RefreshToken 생성  & HashMap 저장
    public String createRefreshToken(Long userId) {
        String refreshToken = Jwts.builder()
                .setHeaderParam("type", "refreshToken")
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Refresh 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME)) // Refresh 토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 리프레시 토큰 저장
        refreshTokenStore.put(userId, refreshToken);
        return refreshToken;
    }

    // HashMap에서 저장된 리프레시 토큰 가져오기
    public String getStoredRefreshToken(Long userId) {
        return refreshTokenStore.get(userId);
    }
    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(Long userId) {
        refreshTokenStore.remove(userId);
    }

    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String token, Long userId) {
        String storedToken = getStoredRefreshToken(userId);
        if (!token.equals(storedToken)) {
            log.warn("리프레시 토큰이 일치하지 않습니다.");
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("리프레시 토큰이 만료되었습니다.");
        } catch (Exception e) {
            log.warn("유효하지 않은 리프레시 토큰입니다.");
        }
        return false;
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        if (isTokenInvalidated(token)) {
            log.warn("무효화된 JWT 토큰입니다: {}", token);
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    // 로그아웃 시 토큰 블랙리스트에 추가
    public void invalidateToken(String token) {
        log.info("로그아웃: 토큰 무효화 -> {}", token);
        invalidatedTokens.add(token);

        // 일정 시간이 지나면 블랙리스트에서 자동 삭제 (만료된 토큰 정리)
        long expirationTime = getTokenExpiration(token);
        if (expirationTime > 0) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    invalidatedTokens.remove(token);
                    log.info("만료된 토큰 제거 -> {}", token);
                }
            }, expirationTime);
        }
    }
    private long getTokenExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }

    // 토큰 블랙리스트 확인
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    // Request 의 Header 에서 token 값을 가져옵니다. "Authorization" : "TOKEN 값"
    public String resolveAccessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : token;
    }

    public String resolveRefreshToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader("Refresh-Token");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : token;
    }

    // UserId 추출
    public Long getUserIdFromToken() {
        String accessToken = resolveAccessToken();
        return getUserIdInToken(accessToken);
    }

    // 토큰에서 userId 추출
    public Long getUserIdInToken(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    // 토큰 parsing
    public Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    // JWT 토큰에서 인증 정보(권한) 조회
    public Authentication getAuthentication(String token) {
        String userId = String.valueOf(getUserIdInToken(token)); //long -> string으로 형변환

        UserDetails userDetails = userDetailService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());  // ""은 비밀번호가 들어갈 자리지만 토큰기반 비밀번호 X
    }
}
