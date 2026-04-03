package jku.se.config;

import jku.se.entity.ROLE;
import jku.se.entity.User;
import jku.se.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User("Admin", "admin@test.at", "admin", ROLE.ADMIN));
                userRepository.save(new User("Lisa", "lisa@test.at", "user", ROLE.USER));
            }
        };
    }
}