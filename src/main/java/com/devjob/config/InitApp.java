package com.devjob.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devjob.model.Role;
import com.devjob.model.User;
import com.devjob.model.UserHasRole;
import com.devjob.common.UserType;
import com.devjob.common.UserStatus;
import com.devjob.repository.RoleRepository;
import com.devjob.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "INIT-APPLICATION")
public class InitApp {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    // Chỉ chạy khi đang chạy trên môi trường dev, database là mysql
    // Tạo role và user mặc định
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner initApplication() {
        log.info("Initializing application.....");
        return args -> {
            Optional<Role> roleUser = roleRepository.findByName("ROLE_" + String.valueOf(UserType.USER));
            if (roleUser.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("ROLE_" + String.valueOf(UserType.USER))
                        .description("User role")
                        .build());
            }

            Optional<Role> roleAdmin = roleRepository.findByName("ROLE_" + String.valueOf(UserType.ADMIN));
            if (roleAdmin.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("ROLE_" + String.valueOf(UserType.ADMIN))
                        .description("Admin role")
                        .build());
            }

            Optional<Role> roleManager = roleRepository.findByName("ROLE_" + String.valueOf(UserType.MANAGER));
            if (roleManager.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("ROLE_" + String.valueOf(UserType.MANAGER))
                        .description("Manager role")
                        .build());
            }

            Optional<Role> roleStaff = roleRepository.findByName("ROLE_" + String.valueOf(UserType.STAFF));
            if (roleStaff.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name("ROLE_" + String.valueOf(UserType.STAFF))
                        .description("Staff role")
                        .build());
            }
            log.info("Role initialization completed .....");

            Optional<User> adminUser = userRepository.findByEmail("admin@gmail.com");
            if (adminUser.isEmpty()) {
                User user = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .status(UserStatus.ACTIVE)
                        .build();

                UserHasRole userHasRole = UserHasRole.builder()
                        .user(user)
                        .role(roleAdmin.get())
                        .build();

                user.setUserHasRoles(Set.of(userHasRole));
                userRepository.save(user);
            }

            Optional<User> basicUser = userRepository.findByEmail("user@gmail.com");
            if (basicUser.isEmpty()) {
                User user = User.builder()
                        .email("user@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .status(UserStatus.ACTIVE)
                        .build();

                UserHasRole userHasRole = UserHasRole.builder()
                        .user(user)
                        .role(roleUser.get())
                        .build();

                user.setUserHasRoles(Set.of(userHasRole));
                userRepository.save(user);
            }
            log.info("User initialization completed .....");
            log.info("Application initialization completed .....");
        };
    }
}
