package net.javaguides.lmms.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.AIRequestDTO;
import net.javaguides.lmms.service.AIService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AIController {


    private final AIService aiService;

    @PostMapping("/ask")
    public String askGeminiAPI(@RequestBody AIRequestDTO aiRequestDTO) {
        return aiService.askGemini(aiRequestDTO);
    }
}
