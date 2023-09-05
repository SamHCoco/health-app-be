package com.samhcoco.healthapp.core.service.impl;

import com.samhcoco.healthapp.core.model.Action;
import com.samhcoco.healthapp.core.model.Message;
import com.samhcoco.healthapp.core.repository.MessageRepository;
import com.samhcoco.healthapp.core.service.EmailService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static com.samhcoco.healthapp.core.enums.MessageStatus.FAILED;
import static com.samhcoco.healthapp.core.enums.MessageStatus.SENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final MessageRepository messageRepository;

    @Override
    public Action send(@NonNull Message message) {
        val action = new Action();
        try {
            val mimeMessage = emailSender.createMimeMessage();
            val helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(message.getSender());
            helper.setTo(message.getRecipient());
            helper.setSubject(message.getSubject());
            helper.setText(message.getBody(), true);

            log.info("Attempting to send email to '{}'.", message.getRecipient());
            emailSender.send(mimeMessage);
            message.setStatus(SENT.name());
            action.setSuccess(true);
            log.info("Successfully sent to email to '{}'", message.getRecipient());

        } catch (Exception e) {
            message.setStatus(FAILED.name());
            action.setMessage(e.getMessage());
            log.debug("Failed to send message with ID '{}' to recipient '{}': {}",
                       message.getId(), message.getRecipient(), e.getMessage());
        }
        messageRepository.save(message);
        return action;
    }
}
