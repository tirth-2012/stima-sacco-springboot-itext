package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.mail.config.ImapProperties;
import com.rutusoft.flowable.mail.dto.EmailData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailPollingService {

    private final ImapProperties properties;
    private final GuarantorService guarantorService;

    @Scheduled(fixedDelayString = "${mail-reader.poll-delay:30000}")
    public void pollMailbox() {

        log.info("Starting mailbox polling...");

        Store store = null;
        Folder inbox = null;

        try {

            Properties props = new Properties();

            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.connectiontimeout", "10000");
            props.put("mail.imaps.timeout", "10000");
            props.put("mail.imaps.writetimeout", "10000");

            Session session = Session.getInstance(props);

            store = session.getStore("imaps");

            // READ FROM application.yaml
            store.connect(
                    properties.getHost(),
                    properties.getPort(),
                    properties.getUsername(),
                    properties.getPassword()
            );

            log.info("Connected to mailbox successfully");

            inbox = store.getFolder("INBOX");

            inbox.open(Folder.READ_WRITE);

            // FETCH UNREAD EMAILS
            FlagTerm unseenFlagTerm =
                    new FlagTerm(new Flags(Flag.SEEN), false);

            Message[] messages = inbox.search(unseenFlagTerm);

            log.info("Unread emails found: {}", messages.length);

            UIDFolder uidFolder = (UIDFolder) inbox;

            for (Message message : messages) {

                try {

                    long uid = uidFolder.getUID(message);

                    log.info("Processing email UID={}", uid);

                    if (shouldSkipEmail(message)) {

                        log.warn("Skipping irrelevant email UID={}", uid);

                        message.setFlag(Flag.SEEN, true);

                        continue;
                    }

                    EmailData emailData = buildEmailData(message);

                    if (emailData == null) {

                        log.warn("EmailData is null for UID={}", uid);

                        continue;
                    }

                    String sender = emailData.getSender();

                    String subject = emailData.getSubject();

                    String body = emailData.getBody();

                    log.info("Sender : {}", sender);
                    log.info("Subject : {}", subject);
                    log.info("Body : {}", body);

                    Long guarantorId = getGuarantorId(subject);
                    log.info("Guarantor ID : {}", guarantorId);
                    if (guarantorId == null) {

                        log.error("Unable to extract guarantorId from subject");

                        message.setFlag(Flag.SEEN, true);

                        continue;
                    }

                    log.info("GuarantorId extracted: {}", guarantorId);

                    GuarantorResponseDto guarantor =
                            guarantorService.getGuarantorById(guarantorId);

                    if (guarantor == null) {

                        log.error("Guarantor not found for id={}", guarantorId);

                        message.setFlag(Flag.SEEN, true);

                        continue;
                    }

//                    // VALIDATE EMAIL SENDER
//                    if (guarantor.getEmail() != null &&
//                            !guarantor.getEmail().equalsIgnoreCase(sender)) {
//
//                        log.warn(
//                                "Unauthorized sender detected. Expected={}, Actual={}",
//                                guarantor.getEmail(),
//                                sender
//                        );
//
//                        message.setFlag(Flag.SEEN, true);
//
//                        continue;
//                    }

                    message.setFlag(Flag.SEEN, true);


                    // EXTRACT ONLY LATEST REPLY
                    String latestReply = extractLatestReply(body);
                    log.info("Latest reply extracted: {}", latestReply);

                    String normalizedReply =
                            latestReply.toLowerCase(Locale.ENGLISH);
                    log.info("Normalized reply : {}", normalizedReply);

                    String status;

                    if (isApproved(normalizedReply)) {

                        status = Status.APPROVED.getCode();

                    } else if (isDeclined(normalizedReply)) {

                        status = Status.REJECTED.getCode();

                    } else {

                        log.warn(
                                "Unable to determine approval decision for guarantor={}",
                                guarantorId
                        );

                        message.setFlag(Flag.SEEN, true);

                        continue;
                    }

                    log.info("Guarantor Id : {}, Approval status : {}", guarantorId, status);


                    guarantorService.updateStatus(
                            guarantorId,
                            status
                    );

                    log.info(
                            "Guarantor status updated successfully. guarantorId={}, status={}",
                            guarantorId,
                            status
                    );

                    // MARK EMAIL AS READ AFTER SUCCESS
                    message.setFlag(Flag.SEEN, true);

                    log.info("Successfully processed UID={}", uid);

                } catch (Exception ex) {

                    log.error("Failed processing individual email", ex);
                }
            }

        } catch (AuthenticationFailedException ex) {

            log.error("Mailbox authentication failed", ex);

        } catch (MessagingException ex) {

            log.error("Mail server communication error", ex);

        } catch (Exception ex) {

            log.error("Unexpected error while polling mailbox", ex);

        } finally {

            try {

                if (inbox != null && inbox.isOpen()) {

                    inbox.close(false);

                    log.info("Inbox closed successfully");
                }

            } catch (Exception ex) {

                log.error("Failed closing inbox", ex);
            }

            try {

                if (store != null && store.isConnected()) {

                    store.close();

                    log.info("Mail store closed successfully");
                }

            } catch (Exception ex) {

                log.error("Failed closing mail store", ex);
            }
        }

        log.info("Mailbox polling completed");
    }

    private boolean shouldSkipEmail(Message message) {

        try {

            String subject = message.getSubject();

            if (subject == null) {
                return true;
            }

            subject = subject.toLowerCase(Locale.ENGLISH);

            // DELIVERY FAILURE
            if (subject.contains("delivery status notification")) {
                return true;
            }

            // ONLY PROCESS GUARANTOR APPROVAL EMAILS
            return !subject.contains("approval request notification");

        } catch (Exception ex) {

            log.error("Failed while validating email subject", ex);
        }

        return true;
    }

    private EmailData buildEmailData(Message message) {

        try {

            String sender = extractSender(message);

            String subject = message.getSubject();

            String body = extractContent(message);

            return EmailData.builder()
                    .sender(sender)
                    .subject(subject)
                    .body(body)
                    .build();

        } catch (Exception ex) {

            log.error("Failed building EmailData", ex);
        }

        return null;
    }

    private String extractSender(Message message) {

        try {

            Address[] addresses = message.getFrom();

            if (addresses != null && addresses.length > 0) {

                InternetAddress address =
                        (InternetAddress) addresses[0];

                return address.getAddress();
            }

        } catch (Exception ex) {

            log.error("Failed extracting sender", ex);
        }

        return "UNKNOWN";
    }

    private String extractContent(Message message) {

        try {

            Object content = message.getContent();

            // SIMPLE TEXT
            if (content instanceof String) {

                return cleanText((String) content);
            }

            // MULTIPART
            if (content instanceof Multipart) {

                return extractMultipart((Multipart) content);
            }

        } catch (Exception ex) {

            log.error("Failed extracting content", ex);
        }

        return "";
    }

    private String extractMultipart(Multipart multipart) {

        try {

            for (int i = 0; i < multipart.getCount(); i++) {

                BodyPart bodyPart = multipart.getBodyPart(i);

                String disposition = bodyPart.getDisposition();

                // SKIP ATTACHMENTS
                if (Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
                    continue;
                }

                // TEXT/PLAIN
                if (bodyPart.isMimeType("text/plain")) {

                    Object content = bodyPart.getContent();

                    if (content != null) {

                        return cleanText(content.toString());
                    }
                }

                // TEXT/HTML
                if (bodyPart.isMimeType("text/html")) {

                    Object content = bodyPart.getContent();

                    if (content != null) {

                        return cleanText(
                                Jsoup.parse(content.toString()).text()
                        );
                    }
                }

                // NESTED MULTIPART
                Object nestedContent = bodyPart.getContent();

                if (nestedContent instanceof Multipart) {

                    return extractMultipart((Multipart) nestedContent);
                }
            }

        } catch (Exception ex) {

            log.error("Failed extracting multipart email", ex);
        }

        return "";
    }

    /**
     * Extract only latest reply from email thread
     */
    private String extractLatestReply(String body) {

        if (body == null) {
            return "";
        }

        String[] splitPatterns = {
                "On .* wrote:",
                "From:",
                "-----Original Message-----"
        };

        String cleaned = body;

        for (String pattern : splitPatterns) {

            cleaned = cleaned.split(pattern)[0];
        }

        return cleanText(cleaned);
    }

    private boolean isApproved(String text) {

        return text.matches(".*\\b(approve|approved|yes|ok)\\b.*");
    }

    private boolean isDeclined(String text) {

        return text.matches(".*\\b(decline|declined|reject|rejected|no)\\b.*");
    }

    private String cleanText(String text) {

        if (text == null) {
            return "";
        }

        return text.replaceAll("\\s+", " ").trim();
    }

    /**
     * Example subject:
     *
     * Approval Request Notification for loan application no 1001 | Guarantor: 12
     */
    private Long getGuarantorId(String subject) {

        try {

            Pattern pattern =
                    Pattern.compile("Guarantor\\s*:\\s*(\\d+)");

            Matcher matcher = pattern.matcher(subject);

            if (matcher.find()) {

                return Long.parseLong(matcher.group(1));
            }

        } catch (Exception ex) {

            log.error("Failed extracting guarantorId from subject", ex);
        }

        return null;
    }
}