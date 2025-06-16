package br.com.italo.huntanalyser.controller;

import br.com.italo.huntanalyser.model.Session;
import br.com.italo.huntanalyser.service.LootParserService;
import br.com.italo.huntanalyser.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loot")
@RequiredArgsConstructor
public class LootController {

    private final LootParserService lootParserService;

    @PostMapping("/parse")
    public ResponseEntity<Session> parseLoot(@RequestBody String text) {
        Session session = lootParserService.parseLootText(text);
        return ResponseEntity.ok(session);
    }

//    @GetMapping("/sessions")
//    public ResponseEntity<List<Session>> getAllSessions() {
//        List<Session> sessions = sessionRepository.findAll();
//        return ResponseEntity.ok(sessions);
//    }
}
