package br.com.italo.huntanalyser.service;

import br.com.italo.huntanalyser.model.*;
import br.com.italo.huntanalyser.repository.PlayerRepository;
import br.com.italo.huntanalyser.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class LootParserService {

    private final SessionRepository sessionRepository;
    private final PlayerRepository playerRepository;
    private final AtomicLong playerIdGenerator = new AtomicLong(1);

    @Transactional
    public Session parseLootText(String text) {
        Session session = new Session();
        List<PlayerSession> playerSessions = new ArrayList<>();

        String[] lines = text.split("\\R");

        for (String line : lines) {
            if (line.startsWith("Session data: From")) {
                String[] parts = line.split("From ");
                String[] sessionParts = parts[1].split(" to ");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
                session.setStartDateTime(LocalDateTime.parse(sessionParts[0].trim(), formatter));
                session.setEndDateTime(LocalDateTime.parse(sessionParts[1].trim(), formatter));
            } else if (line.startsWith("Session")) {
                String[] sessionDetails = line.split("Session: ");
                session.setDuration(sessionDetails[1].replace("h", ""));
            } else if (line.startsWith("Supplies:")) {
                String[] sessionDetails = line.split("Supplies: ");
                session.setTotalSupplies(parseValue(sessionDetails[1].trim()));
            } else if (line.startsWith("Loot Type:")) {
                String[] sessionDetails = line.split("Loot Type: ");
                session.setLootType(sessionDetails[1].trim().toUpperCase());
            } else if (line.startsWith("Loot:")) {
                String[] sessionDetails = line.split("Loot: ");
                session.setTotalLoot(parseValue(sessionDetails[1].trim()));
            } else if (line.startsWith("Balance:")) {
                String[] sessionDetails = line.split("Balance: ");
                session.setTotalBalance(parseValue(sessionDetails[1].trim()));
            } else {
                parsePlayerData(session, playerSessions, line);
            }
        }

        session.setPlayers(playerSessions);
        return sessionRepository.save(session);
    }

    private void parsePlayerData(Session session, List<PlayerSession> playerSessions, String line) {
        // Identifica início de um novo player
        if (!line.startsWith("\t") && !line.isBlank()) {
            // Extrai nome e se é líder
            String playerName = line.replace("(Leader)", "").trim();
            boolean isLeader = line.contains("(Leader)");

            // Busca ou cria o player
            Player player = verifyPlayerIdExists(playerName);

            // Cria nova instância de PlayerSession
            PlayerSession playerSession = new PlayerSession();
            playerSession.setPlayer(player);
            playerSession.setLeader(isLeader);
            playerSession.setSession(session);

            // Adiciona à lista temporária para preencher os dados nas próximas linhas
            playerSessions.add(playerSession);
        } else if (line.startsWith("\tLoot:")) {
            getLast(playerSessions).setLoot(parseValue(line.split("\tLoot: ")[1].trim()));
        } else if (line.startsWith("\tSupplies:")) {
            getLast(playerSessions).setSupplies(parseValue(line.split("\tSupplies: ")[1].trim()));
        } else if (line.startsWith("\tBalance:")) {
            getLast(playerSessions).setBalance(parseValue(line.split("\tBalance: ")[1].trim()));
        } else if (line.startsWith("\tDamage:")) {
            getLast(playerSessions).setDamage(parseValue(line.split("\tDamage: ")[1].trim()));
        } else if (line.startsWith("\tHealing:")) {
            getLast(playerSessions).setHealing(parseValue(line.split("\tHealing: ")[1].trim()));
        }
    }

    // Helper para pegar o último playerSession adicionado
    private PlayerSession getLast(List<PlayerSession> list) {
        return list.get(list.size() - 1);
    }

    private Player verifyPlayerIdExists(String playerName) {
        return playerRepository.findById_PlayerName(playerName).orElseGet(() -> {
            Player newPlayer = new Player();
            newPlayer.setId(new PlayerId(playerIdGenerator.getAndIncrement(), playerName));
            return playerRepository.save(newPlayer);
        });
    }

    private Long parseValue(String value) {
        try {
            return Long.parseLong(value.replace(",", ""));
        } catch (NumberFormatException e) {
            log.error("Erro ao converter valor: " + value, e);
            return 0L; // Valor padrão em caso de erro
        }
    }
}
