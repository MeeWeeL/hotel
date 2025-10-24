package com.acme.booking.config;

import com.acme.booking.model.Role;
import com.acme.booking.model.User;
import com.acme.booking.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initUsers(
            UserRepo users,
            PasswordEncoder encoder
    ) {
        return args -> {
            if (users.findByUsername("admin").isEmpty()) {
                users.save(
                        User.builder()
                                .username("admin")
                                .password(encoder.encode("admin"))
                                .role(Role.ADMIN)
                                .build()
                );
            }
            if (users.findByUsername("user").isEmpty()) {
                users.save(
                        User.builder()
                                .username("user")
                                .password(encoder.encode("user"))
                                .role(Role.USER)
                                .build()
                );
            }
        };
    }
}
