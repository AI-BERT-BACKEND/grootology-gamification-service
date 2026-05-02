package com.aibert.dosw.entrypoints.rest.controller;

import com.aibert.dosw.application.dto.request.ActionEventRequestDTO;
import com.aibert.dosw.application.dto.response.GamificationResponseDTO;
import com.aibert.dosw.domain.ports.in.GamificationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationUseCase gamificationUseCase;

    @PostMapping("/{userId}/event")
    public ResponseEntity<GamificationResponseDTO> processEvent(
            @PathVariable UUID userId,
            @Valid @RequestBody ActionEventRequestDTO request) {
        return ResponseEntity.ok(gamificationUseCase.processEvent(userId, request));
    }

    @GetMapping("/{userId}/progress")
    public ResponseEntity<GamificationResponseDTO> getProgress(@PathVariable UUID userId) {
        return ResponseEntity.ok(gamificationUseCase.getProgress(userId));
    }
}
