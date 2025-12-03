package net.javaguides.lmms.service;

import net.javaguides.lmms.dto.LoginRequestDTO;
import net.javaguides.lmms.dto.RegisterRequestDTO;

public interface AuthService {
    String login(LoginRequestDTO dto);
    String register(RegisterRequestDTO dto);
}
