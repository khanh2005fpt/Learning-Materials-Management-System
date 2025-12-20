package net.javaguides.lmms.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.AIRequestDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final Client client;
    public String askGemini(AIRequestDTO aiRequestDTO)
    {
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        aiRequestDTO.getQuestion(),
                        null);

        return response.text();
    }


}