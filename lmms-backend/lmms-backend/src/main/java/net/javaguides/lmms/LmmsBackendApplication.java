package net.javaguides.lmms;

import net.javaguides.lmms.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LmmsBackendApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(LmmsBackendApplication.class, args);

        JwtService jwtService = context.getBean(JwtService.class);

        String username = "Lala";
        String role = "USER";

        // 1. Tạo token
        String token = jwtService.generateToken(username, role);
        System.out.println("Token: " + token);

//        // 2. Lấy username từ token
//        String extractedUsername = jwtService.extractUsername(token);
//        System.out.println("Username extracted: " + extractedUsername);
//
//        // 3. Lấy role từ token
//        String extractedRole = jwtService.extractRole(token);
//        System.out.println("Role extracted: " + extractedRole);
    }

}
