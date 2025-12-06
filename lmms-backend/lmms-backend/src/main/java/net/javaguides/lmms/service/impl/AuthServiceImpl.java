package net.javaguides.lmms.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.LoginRequestDTO;
import net.javaguides.lmms.dto.RegisterRequestDTO;
import net.javaguides.lmms.entity.Role;
import net.javaguides.lmms.entity.User;
import net.javaguides.lmms.repository.UserRepository;
import net.javaguides.lmms.service.AuthService;
import net.javaguides.lmms.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
    @Override
    public ResponseEntity<?> login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByUsername(requestDTO.getUsername());
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not found"));
        }
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Wrong password"));
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole().name()
        ));
    }


    @Override
    public String register(RegisterRequestDTO requestDTO) {
        if(userRepository.existsByEmail(requestDTO.getEmail())) {
            return "Email already exists";
        }
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            return "Username already exists";
        }

        User user = new User();
        user.setFullname(requestDTO.getFullname());
        user.setEmail(requestDTO.getEmail());
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return "User registered successfully";
    }
}
