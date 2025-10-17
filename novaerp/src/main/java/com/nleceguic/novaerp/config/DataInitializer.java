package com.nleceguic.novaerp.config;

import com.nleceguic.novaerp.entity.Role;
import com.nleceguic.novaerp.entity.User;
import com.nleceguic.novaerp.repository.RoleRepository;
import com.nleceguic.novaerp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("🟢 Inicializando datos base (roles y usuario admin)...");

        List<String> defaultRoles = List.of("ADMIN", "EMPLEADO", "CONTABLE", "RRHH");

        for (String roleName : defaultRoles) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(roleName);
                roleRepository.save(newRole);
                System.out.println("🟩 Rol creado: " + roleName);
                return newRole;
            });
        }

        if (userRepository.findByEmail("admin@novaerp.com").isEmpty()) {
            Role adminRole = roleRepository.findByName("ADMIN").get();

            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@novaerp.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(new HashSet<>() {
                {
                    add(adminRole);
                }
            });

            userRepository.save(admin);
            System.out.println("🟩 Usuario administrador creado: admin@novaerp.com / admin123");
        } else {
            System.out.println("✅ Usuario administrador ya existe, no se recreará.");
        }

        System.out.println("✅ Inicialización completada.");
    }
}
