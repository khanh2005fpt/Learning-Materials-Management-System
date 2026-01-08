package net.javaguides.lmms.controller;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.AIRequestDTO;
import net.javaguides.lmms.dto.AIResponseDTO;
import net.javaguides.lmms.dto.FullAIResponseDTO;
import net.javaguides.lmms.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AIController {


    private final AIService aiService;

    @PostMapping("/search")
    public List<AIResponseDTO> search(@RequestBody AIRequestDTO aiRequestDTO) {
        return aiService.searchPages(aiRequestDTO);
    }

    @PostMapping("/ask")
    public FullAIResponseDTO askGeminiAPI(@RequestBody AIRequestDTO aiRequestDTO) {
        return aiService.askGemini(aiRequestDTO);
    }
}
