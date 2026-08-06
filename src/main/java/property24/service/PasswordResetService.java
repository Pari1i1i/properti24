package property24.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import property24.entity.PasswordResetToken;
import property24.entity.User;
import property24.repository.PasswordResetTokenRepository;
import property24.repository.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    /**
     * Memproses permintaan reset password: buat OTP 6 digit & kirim email.
     */
    public void generateAndSendOtp(String email) {
        String trimmedEmail = email.trim();
        Optional<User> userOpt = userRepository.findByUsername(trimmedEmail);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Email/Username tidak terdaftar dalam sistem!");
        }

        // Generate 6 digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(trimmedEmail);
        token.setOtp(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setUsed(false);

        tokenRepository.save(token);
        emailService.sendOtpEmail(trimmedEmail, otp);
    }

    /**
     * Verifikasi kode OTP.
     */
    public boolean verifyOtp(String email, String otp) {
        String trimmedEmail = email.trim();
        String trimmedOtp = otp.trim();

        Optional<PasswordResetToken> tokenOpt = tokenRepository
                .findTopByEmailAndOtpAndUsedFalseOrderByCreatedAtDesc(trimmedEmail, trimmedOtp);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        return !token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    /**
     * Update password pengguna jika OTP valid.
     */
    public void resetPassword(String email, String otp, String newPassword) {
        String trimmedEmail = email.trim();
        String trimmedOtp = otp.trim();

        if (!verifyOtp(trimmedEmail, trimmedOtp)) {
            throw new IllegalArgumentException("Kode OTP salah atau sudah kedaluwarsa!");
        }

        Optional<User> userOpt = userRepository.findByUsername(trimmedEmail);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User tidak ditemukan!");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark OTP token as used
        Optional<PasswordResetToken> tokenOpt = tokenRepository
                .findTopByEmailAndOtpAndUsedFalseOrderByCreatedAtDesc(trimmedEmail, trimmedOtp);
        tokenOpt.ifPresent(token -> {
            token.setUsed(true);
            tokenRepository.save(token);
        });

        log.info("Password untuk user [{}] berhasil diperbarui.", trimmedEmail);
    }
}
