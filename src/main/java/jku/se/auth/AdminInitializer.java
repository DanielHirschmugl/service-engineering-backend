package jku.se.auth;

import jku.se.user.Role;
import jku.se.user.User;
import jku.se.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.firstname:Admin}")
    private String adminFirstname;

    @Value("${app.admin.lastname:User}")
    private String adminLastname;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.phonenumber:}")
    private String adminPhoneNumber;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println("Admin-Benutzer existiert bereits.");
            return;
        }

        User admin = User.builder()
                .firstname(adminFirstname)
                .lastname(adminLastname)
                .email(adminEmail)
                .phonenumber(adminPhoneNumber)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        System.out.println("Admin-Benutzer wurde automatisch erstellt.");
    }
}