package net.javaguides.lmms.service.impl;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.dto.LoginRequestDTO;
import net.javaguides.lmms.dto.RegisterRequestDTO;
import net.javaguides.lmms.entity.User;
import net.javaguides.lmms.repository.UserRepository;
import net.javaguides.lmms.service.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Override
    public String login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByUsername(requestDTO.getUsername());
        if (user == null) {
            return "User not found";
        }
        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword()))  //So sánh mật khẩu được nhập vào và mật khẩu được mã hóa
        {
            return "Wrong password";
        }
        return "Login Successful";
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
        userRepository.save(user);
        return "User registered successfully";
    }
}
