package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.SessionDto;
import com.agustin.backend_dialysis_record.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    //set inactive

    @PostMapping
    public ResponseEntity<SessionDto> create(@RequestBody SessionDto sessionDto) {
        SessionDto session = sessionService.create(sessionDto);
        return ResponseEntity.ok().body(session);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getAllSession() {
        List<SessionDto> sessions = sessionService.findAll();
        return ResponseEntity.ok().body(sessions);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable long sessionId) {
        SessionDto session = sessionService.findById(sessionId);
        return ResponseEntity.ok().body(session);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionDto> update(@PathVariable long sessionId, @RequestBody SessionDto sessionDto) {
        SessionDto session = sessionService.update(sessionId, sessionDto);
        return ResponseEntity.ok().body(session);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable long sessionId) {
        sessionService.delete(sessionId);
        return ResponseEntity.noContent().build();
    }
}
