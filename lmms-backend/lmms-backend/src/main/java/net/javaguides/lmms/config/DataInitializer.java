package net.javaguides.lmms.config;

import lombok.RequiredArgsConstructor;
import net.javaguides.lmms.entity.Role;
import net.javaguides.lmms.entity.User;
import net.javaguides.lmms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    //Tạo tài khoản admin cho hệ thống
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setFullname("Admin");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu mặc định
            admin.setRole(Role.ADMIN);
            admin.setEmail("admin@example.com");
            userRepository.save(admin);
         //   System.out.println("Admin account created: username=admin, password=admin123");
        }
    }
}
