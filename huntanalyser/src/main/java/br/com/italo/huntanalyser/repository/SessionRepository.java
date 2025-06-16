package br.com.italo.huntanalyser.repository;

import br.com.italo.huntanalyser.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAll();

    @Query("SELECT DISTINCT s FROM Session s " +
            "JOIN s.players ps " +
            "JOIN ps.player p " +
            "WHERE p.id.playerName LIKE %:playerName%")
    List<Session> findSessionsByPlayerNameContaining(@Param("playerName") String playerName);

}
