package net.javaguides.lmms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponseDTO {
    private String bookTitle;
    private int pageNumber;
    private String content;

}
