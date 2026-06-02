package com.rutusoft.flowable.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailNotificationUtil {
    @Value("${admin.email:sg.vadaviya@gmail.com}")
    private String adminEmail;

    @Value("${admin.notification-enabled:false}")
    private boolean notificationEnabled;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async("emailTaskExecutor")
    public void sendEmail(String to, String subject, String templateName, Context context) {
        String htmlContent = templateEngine.process(templateName, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            log.info("Notification enabled : {}", notificationEnabled);
            log.info("adminEmail : {}", adminEmail);

            helper = new MimeMessageHelper(message, true, "UTF-8");

            if (notificationEnabled) {
                helper.setTo(new String[]{to, adminEmail});
            } else {
                helper.setTo(to);
            }

            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email notification to : {}",to, e);
            //throw new RuntimeException(e);
        }

        log.info("Email sent successfully to {}", to);
    }

    @Async("emailTaskExecutor")
    public void sendEmailWithAttachment(String to,
                                        String subject,
                                        String text,
                                        byte[] fileData,
                                        String fileName) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            log.info("Notification enabled : {}", notificationEnabled);
            log.info("adminEmail : {}", adminEmail);

            if (notificationEnabled) {
                helper.setTo(new String[]{to, adminEmail});
            } else {
                helper.setTo(to);
            }

            helper.setSubject(subject);

            // plain text email (no thymeleaf template)
            helper.setText(text, false);

            // 📎 attachment
            helper.addAttachment(fileName, new org.springframework.core.io.ByteArrayResource(fileData));

            mailSender.send(message);

            log.info("Email with attachment sent successfully to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email with attachment to : {}", to, e);
        }
    }
}
