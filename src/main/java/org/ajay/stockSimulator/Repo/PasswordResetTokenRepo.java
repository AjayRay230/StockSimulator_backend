package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.PasswordResetToken;
import org.ajay.stockSimulator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepo
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
