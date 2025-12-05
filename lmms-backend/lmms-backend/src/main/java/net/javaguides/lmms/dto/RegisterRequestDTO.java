package net.javaguides.lmms.dto;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String fullname;
    private String email;
    private String username;
    private String password;
}
