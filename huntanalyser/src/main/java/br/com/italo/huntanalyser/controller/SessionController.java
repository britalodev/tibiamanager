package br.com.italo.huntanalyser.controller;

import br.com.italo.huntanalyser.model.Session;
import br.com.italo.huntanalyser.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    @Autowired
    private final SessionRepository sessionRepository;

    @GetMapping("/by-player")
    public ResponseEntity<List<Session>> getSessionsByPlayer(@RequestParam String playerName) {
        List<Session> sessions = sessionRepository.findSessionsByPlayerNameContaining(playerName);
        return ResponseEntity.ok(sessions);
    }
}
