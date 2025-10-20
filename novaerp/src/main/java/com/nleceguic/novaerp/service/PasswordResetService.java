package com.nleceguic.novaerp.service;

import com.nleceguic.novaerp.entity.PasswordResetToken;
import com.nleceguic.novaerp.entity.User;
import com.nleceguic.novaerp.repository.PasswordResetTokenRepository;
import com.nleceguic.novaerp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String createPasswordResetToken(String email, int expireMinutes) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe usuario con ese email"));
        tokenRepository.deleteAllByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(expireMinutes));
        prt.setUsed(false);
        tokenRepository.save(prt);

        return token;
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Token inválido");
        PasswordResetToken prt = opt.get();
        if (prt.isUsed())
            throw new IllegalArgumentException("Token ya fue utilizado");
        if (prt.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Token expirado");

        return prt;
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = validatePasswordResetToken(token);
        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        prt.setUsed(true);
        tokenRepository.save(prt);
    }
}
