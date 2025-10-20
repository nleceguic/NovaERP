package com.nleceguic.novaerp.controller;

import com.nleceguic.novaerp.dto.AuthResponse;
import com.nleceguic.novaerp.dto.LoginRequest;
import com.nleceguic.novaerp.dto.RegisterRequest;
import com.nleceguic.novaerp.entity.SessionAudit;
import com.nleceguic.novaerp.entity.User;
import com.nleceguic.novaerp.repository.SessionAuditRepository;
import com.nleceguic.novaerp.service.UserService;
import com.nleceguic.novaerp.util.JwtUtil;
import com.nleceguic.novaerp.util.SimpleRateLimiter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.nleceguic.novaerp.entity.Pair;

import java.time.LocalDateTime;
import java.util.Set;

import com.nleceguic.novaerp.dto.ForgotPasswordRequest;
import com.nleceguic.novaerp.dto.ResetPasswordRequest;
import com.nleceguic.novaerp.service.PasswordResetService;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    //private final PasswordEncoder passwordEncoder;
    private final SessionAuditRepository auditRepository;
    private final PasswordResetService passwordResetService;
    //private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final SimpleRateLimiter emailLimiter = new SimpleRateLimiter(3, Duration.ofHours(1));
    private final SimpleRateLimiter ipLimiter = new SimpleRateLimiter(10, Duration.ofHours(1));

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.getNombre(),
                request.getEmail(),
                request.getPassword(),
                request.getRole());
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String email) {
        // Falta actualizar la sesion de SessionAudit.
        return ResponseEntity.ok("Usuario desconectado exitosamente");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req, HttpServletRequest httpReq) {
        String email = req.getEmail();
        String ip = httpReq.getRemoteAddr();

        if (!emailLimiter.isAllowed(email) || !ipLimiter.isAllowed(ip)) {
            return ResponseEntity.status(429).body("Too many requests. Try later.");
        }

        try {
            Pair p = passwordResetService.createPasswordResetToken(email, 60);
            String resetUrl = "http://localhost:3000/reset-password?tokenId=" + p.tokenId + "&token=" + p.tokenSecret;
            logger.info("Password reset link for {}: {}", email, resetUrl);
        } catch (IllegalArgumentException e) {
            logger.info("Password reset requested for unknown email: {}", email);
        }

        return ResponseEntity.ok("Si el email existe, se ha enviado un enlace para restablecer la contraseña.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            passwordResetService.resetPassword(req.getTokenId(), req.getToken(), req.getNewPassword());
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
