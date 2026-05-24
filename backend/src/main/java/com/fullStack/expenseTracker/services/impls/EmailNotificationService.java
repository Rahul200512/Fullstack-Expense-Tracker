package com.fullStack.expenseTracker.services.impls;
import java.io.UnsupportedEncodingException;

import com.fullStack.expenseTracker.services.NotificationService;
import com.fullStack.expenseTracker.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationService implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public void sendUserRegistrationVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        log.info("==== VERIFICATION CODE for {} : {} ====", user.getEmail(), user.getVerificationCode());
        try {
            String toAddress = user.getEmail();
            String fromAddress = fromMail;
            String senderName = "Company";
            String subject = "Please verify your registration";
            String content = "Dear " + user.getUsername() + ",<br><br>"
                    + "<p>Thank you for joining us! We are glad to have you on board.</p><br>"
                    + "<p>To complete the sign up process, enter the verification code in your device.</p><br>"
                    + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                    + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                    + "<br>Thank you,<br>"
                    + "Your company name.";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);

            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.warn("Email sending skipped (not configured): {}", e.getMessage());
        }
    }


    public void sendForgotPasswordVerificationEmail(User user) throws MessagingException, UnsupportedEncodingException {
        log.info("==== FORGOT PASSWORD CODE for {} : {} ====", user.getEmail(), user.getVerificationCode());
        try {
            String toAddress = user.getEmail();
            String fromAddress = fromMail;
            String senderName = "Company";
            String subject = "Forgot password - Please verify your Account";
            String content = "Dear " + user.getUsername() + ",<br><br>"
                    + "<p>To change your password, enter the verification code in your device.</p><br>"
                    + "<p>verification code: <strong>" + user.getVerificationCode() + "</strong></p><br>"
                    + "<p><strong>Please note that the above verification code will be expired within 15 minutes.</strong></p>"
                    + "<br>Thank you,<br>"
                    + "Your company name.";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);

            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.warn("Email sending skipped (not configured): {}", e.getMessage());
        }
    }
}
