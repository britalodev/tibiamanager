package br.com.italo.huntanalyser.repository;

import br.com.italo.huntanalyser.model.PlayerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerSessionRepository extends JpaRepository<PlayerSession, Long> {
}
