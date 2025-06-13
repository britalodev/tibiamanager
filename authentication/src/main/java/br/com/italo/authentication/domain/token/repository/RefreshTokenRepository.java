package br.com.italo.authentication.domain.token.repository;

import br.com.italo.authentication.domain.token.entity.RefreshToken;
import br.com.italo.authentication.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
