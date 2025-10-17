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
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final SessionAuditRepository auditRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = userService.findByEmail(request.getEmail());
            Set<String> roles = user.getRoles().stream().map(r -> r.getName())
                    .collect(java.util.stream.Collectors.toSet());
            String token = jwtUtil.generateToken(user.getEmail(), roles);

            SessionAudit audit = new SessionAudit();
            audit.setEmail(user.getEmail());
            audit.setIpAddress(httpRequest.getRemoteAddr());
            audit.setLoginTime(LocalDateTime.now());
            auditRepository.save(audit);

            return ResponseEntity.ok(new AuthResponse(token, user.getEmail()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }
}
