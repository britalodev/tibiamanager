package br.com.italo.huntanalyser.controller;

import br.com.italo.huntanalyser.dto.DivisionResult;
import br.com.italo.huntanalyser.model.Session;
import br.com.italo.huntanalyser.repository.SessionRepository;
import br.com.italo.huntanalyser.service.LootDivisionService;
import br.com.italo.huntanalyser.service.LootParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/loot")
@RequiredArgsConstructor
public class LootController {

    private final LootParserService lootParserService;
    private final LootDivisionService lootDivisionService;
    private final SessionRepository sessionRepository;

    @PostMapping("/parse")
    public ResponseEntity<DivisionResult> parseLoot(@RequestBody String text) {
        // 1. Fazer o parse do texto e salvar no banco
        Session session = lootParserService.parseLootText(text);

        // 2. Calcular a divisão do loot
        DivisionResult divisionResult = lootDivisionService.calculateLootDivision(session);

        return ResponseEntity.ok(divisionResult);
    }

    @GetMapping("/division/{sessionId}")
    public ResponseEntity<DivisionResult> calculateDivision(@PathVariable Long sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Session session = sessionOpt.get();
        DivisionResult divisionResult = lootDivisionService.calculateLootDivision(session);

        return ResponseEntity.ok(divisionResult);
    }

    @GetMapping
    public ResponseEntity<List<Session>> getAll() {
        List<Session> sessions = sessionRepository.findAll();
        return ResponseEntity.ok(sessions.isEmpty() ? null : sessions);
    }
}
