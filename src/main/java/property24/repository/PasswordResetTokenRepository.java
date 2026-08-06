package property24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property24.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
    Optional<PasswordResetToken> findTopByEmailAndOtpAndUsedFalseOrderByCreatedAtDesc(String email, String otp);
}
