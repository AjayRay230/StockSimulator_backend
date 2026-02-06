package org.ajay.stockSimulator.service;

import lombok.RequiredArgsConstructor;
import org.ajay.stockSimulator.Repo.PasswordResetTokenRepo;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.PasswordResetToken;
import org.ajay.stockSimulator.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepo userRepo;
    private final PasswordResetTokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Frontend base URL (from env variable)
    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final int EXPIRY_MINUTES = 15;

    public void forgotPassword(String email) {

        User user = userRepo.findByEmail(email);

        // SECURITY: do not reveal whether user exists
        if (user == null) return;

        // remove old token if any
        tokenRepo.deleteByUser(user);

        // generate new token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(EXPIRY_MINUTES));

        tokenRepo.save(token);

        // build reset link using deployed frontend URL
        String resetLink =
                frontendUrl + "/reset-password?token=" + token.getToken();

        // send email
        emailService.sendResetLink(user.getEmail(), resetLink);
    }

    public void resetPassword(String tokenValue, String newPassword) {

        PasswordResetToken token = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // invalidate token after successful reset
        tokenRepo.delete(token);
    }
}
