package com.nleceguic.novaerp.service;

import com.nleceguic.novaerp.entity.Role;
import com.nleceguic.novaerp.entity.User;
import com.nleceguic.novaerp.repository.RoleRepository;
import com.nleceguic.novaerp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password, String roleName) {
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email ya registrado");

        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Rol no existe"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(new HashSet<>() {
            {
                add(role);
            }
        });

        return userRepository.save(user);
    }
}