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

    public Pair createPasswordResetToken(String email, int expireMinutes) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No existe usuario con ese email"));
        tokenRepository.deleteAllByUserId(user.getId());

        String tokenId = UUID.randomUUID().toString();
        String tokenSecret = UUID.randomUUID().toString();
        String hash = passwordEncoder.encode(tokenSecret);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setTokenId(tokenId);
        prt.setTokenHash(hash);
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusMinutes(expireMinutes));
        prt.setUsed(false);
        tokenRepository.save(prt);

        return new Pair(tokenId, tokenSecret);
    }

    public PasswordResetToken validateTokenAndGet(String tokenId, String tokenSecret) {
        Optional<PasswordResetToken> opt = tokenRepository.findByTokenId(tokenId);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Token inválido");
        PasswordResetToken prt = opt.get();
        if (prt.isUsed())
            throw new IllegalArgumentException("Token ya usado");
        if (prt.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Token expirado");
        if (!passwordEncoder.matches(tokenSecret, prt.getTokenHash()))
            throw new IllegalArgumentException("Token inválido");

        return prt;
    }

    public void resetPassword(String tokenId, String tokenSecret, String newPassword) {
        PasswordResetToken prt = validateTokenAndGet(tokenId, tokenSecret);
        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        prt.setUsed(true);
        tokenRepository.save(prt);
    }

    public static class Pair {
        public final String tokenId;
        public final String tokenSecret;

        public Pair(String tokenId, String tokenSecret) {
            this.tokenId = tokenId;
            this.tokenSecret = tokenSecret;
        }
    }
}
