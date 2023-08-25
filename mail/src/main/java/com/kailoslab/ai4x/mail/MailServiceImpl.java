package com.kailoslab.ai4x.mail;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;
    @Value("${kailoslab.mail.user:}")
    private String user;

    @Override
    public boolean sendMail(MailDetail mailDetail) {
        // Creating a Mime Message
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachment to be sent
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            if(StringUtils.isEmpty(user)) {
                mimeMessageHelper.setFrom(sender);
            } else {
                try {
                    mimeMessageHelper.setFrom(new InternetAddress(sender,user));
                } catch (UnsupportedEncodingException e) {
                    mimeMessageHelper.setFrom(sender);
                }
            }
            mimeMessageHelper.setTo(mailDetail.getRecipient());
            mimeMessageHelper.setSubject(mailDetail.getSubject());
            mimeMessageHelper.setText(mailDetail.getMsgBody(), true);

            if(ObjectUtils.isNotEmpty(mailDetail.getAttachment())) {
                // Adding the file attachment
                mailDetail.getAttachment().forEach(attachment -> {
                    FileSystemResource file = new FileSystemResource(new File(attachment));
                    try {
                        mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                    } catch (MessagingException e) {
                        log.error("Cannot attach a file ({})!", attachment);
                    }
                });
            }

            // Sending the email with attachment
            mailSender.send(mimeMessage);
            log.info("Email has been sent successfully to {}.", mailDetail.getRecipient());
            return true;
        }

        // Catch block to handle the MessagingException
        catch (MessagingException e) {
            log.error("Error while Sending email to {}!", mailDetail.getRecipient());
            return false;
        }
    }
}
