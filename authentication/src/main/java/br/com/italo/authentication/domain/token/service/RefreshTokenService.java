package br.com.italo.authentication.domain.token.service;

import br.com.italo.authentication.domain.token.entity.RefreshToken;
import br.com.italo.authentication.domain.token.repository.RefreshTokenRepository;
import br.com.italo.authentication.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenExpirationMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
