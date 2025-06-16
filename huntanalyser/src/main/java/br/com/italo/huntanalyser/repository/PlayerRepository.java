package br.com.italo.huntanalyser.repository;

import br.com.italo.huntanalyser.model.Player;
import br.com.italo.huntanalyser.model.PlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, PlayerId> {

    Optional<Player> findById_PlayerName(String playerName);

}
