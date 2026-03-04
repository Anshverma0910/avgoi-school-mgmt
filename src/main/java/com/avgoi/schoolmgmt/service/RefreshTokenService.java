package com.avgoi.schoolmgmt.service;

import com.avgoi.schoolmgmt.entity.RefreshToken;
import com.avgoi.schoolmgmt.entity.User;
import com.avgoi.schoolmgmt.repository.RefreshTokenRepository;
import com.avgoi.schoolmgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration-ms:2592000000}")
    private long refreshExpirationMs;

    @Transactional
    public String createAndSave(User user) {
        String token = UUID.randomUUID().toString();
        Instant now = Instant.now();
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(token);
        rt.setExpiresAt(now.plusMillis(refreshExpirationMs));
        rt.setCreatedAt(now);
        refreshTokenRepository.save(rt);
        return token;
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByValidToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(RefreshToken::getUser);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
