package com.nleceguic.novaerp.service;

import com.nleceguic.novaerp.entity.User;
import com.nleceguic.novaerp.entity.Role;
import com.nleceguic.novaerp.repository.UserRepository;
import com.nleceguic.novaerp.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                Set<GrantedAuthority> authorities = user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toSet());

                return new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPassword(),
                                authorities);
        }

        public User findByEmail(String email) {
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        }

        public User registerUser(String nombre, String email, String password, String roleName) {
                if (userRepository.existsByEmail(email)) {
                        throw new RuntimeException("El email ya está registrado: " + email);
                }

                Role role = roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));

                User user = new User();
                user.setName(nombre);
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
