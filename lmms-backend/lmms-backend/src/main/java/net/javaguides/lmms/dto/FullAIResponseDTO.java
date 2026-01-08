package net.javaguides.lmms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullAIResponseDTO {
    private String answer;
    private List<AIResponseDTO> aiResponseDTOListPage;
}
