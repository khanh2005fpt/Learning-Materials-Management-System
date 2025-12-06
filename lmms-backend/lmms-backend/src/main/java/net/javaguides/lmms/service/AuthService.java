package net.javaguides.lmms.service;

import net.javaguides.lmms.dto.LoginRequestDTO;
import net.javaguides.lmms.dto.RegisterRequestDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequestDTO dto);
    String register(RegisterRequestDTO dto);
}
