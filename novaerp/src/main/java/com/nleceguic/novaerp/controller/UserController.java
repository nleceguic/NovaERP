package com.nleceguic.novaerp.controller;

import com.nleceguic.novaerp.dto.AuthResponse;
import com.nleceguic.novaerp.dto.LoginRequest;
import com.nleceguic.novaerp.dto.RegisterRequest;
import com.nleceguic.novaerp.entity.SessionAudit;
import com.nleceguic.novaerp.entity.User;
import com.nleceguic.novaerp.repository.SessionAuditRepository;
import com.nleceguic.novaerp.service.UserService;
import com.nleceguic.novaerp.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import com.nleceguic.novaerp.dto.UserDTO;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAllUsers().stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
    }
}

