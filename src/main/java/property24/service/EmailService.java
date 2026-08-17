package property24.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static org.reflections.Reflections.log;

@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        log.info("=================================================");
        log.info(" KODE OTP FORGOT PASSWORD : [{}] UNTUK EMAIL : [{}]", otpCode, toEmail);
        log.info("=================================================");

        if (mailSender == null) {
            log.warn("JavaMailSender belum dikonfigurasi. Menggunakan console fallback.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Kode OTP Reset Password - Property 24");

            String htmlMsg = "<div style=\"font-family: 'Segoe UI', Helvetica, Arial, sans-serif; background-color: #f4f8f5; padding: 30px; border-radius: 12px;\">" +
                    "<div style=\"max-width: 500px; margin: 0 auto; background: #ffffff; padding: 28px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08);\">" +
                    "<h2 style=\"color: #1c3b2e; margin-top: 0; font-size: 22px; font-weight: 800; letter-spacing: 1px;\">PROPERTY 24</h2>" +
                    "<h3 style=\"color: #2b5443; margin-top: 0;\">Reset Password Account</h3>" +
                    "<p style=\"color: #444444; font-size: 14px; line-height: 1.5;\">Halo,</p>" +
                    "<p style=\"color: #444444; font-size: 14px; line-height: 1.5;\">Permintaan untuk mereset password akun Anda (<b>" + toEmail + "</b>) telah diterima. Gunakan kode OTP berikut untuk memverifikasi identitas Anda:</p>" +
                    "<div style=\"background: #1c3b2e; color: #8fb08a; padding: 18px; text-align: center; font-size: 32px; font-weight: 800; letter-spacing: 8px; border-radius: 8px; margin: 24px 0;\">" +
                    otpCode +
                    "</div>" +
                    "<p style=\"color: #666666; font-size: 13px;\">Kode OTP ini berlaku selama <b>10 menit</b>. Mohon untuk tidak memberitahukan kode ini kepada siapapun.</p>" +
                    "<hr style=\"border: none; border-top: 1px solid #e0e8e3; margin: 24px 0;\" />" +
                    "<p style=\"color: #999999; font-size: 12px; text-align: center;\">&copy; Property 24 - Asset Management System</p>" +
                    "</div></div>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
            log.info("Berhasil mengirim email OTP ke {}", toEmail);
        } catch (Exception e) {
            log.error("Gagal mengirim email via SMTP Gmail ({}), namun kode OTP tercatat pada log server.", e.getMessage());
        }
    }
}
