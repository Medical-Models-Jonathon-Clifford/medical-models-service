package org.jono.medicalmodelsservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/emails")
public class EmailController {

    @Value("${to.address}")
    public String toAddress;

    private final EmailService emailService;
    private final String attachmentPath;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
        this.attachmentPath = Objects.requireNonNull(getClass().getClassLoader().getResource("images/50-note-1.jpg")).getPath();
    }

    @PostMapping(path = "/simple")
    public void handleSimpleEmail() {
        emailService.sendSimpleMessage(toAddress,
                "Simple Email",
                "This is a test message sent from Spring.");
    }

    @PostMapping(path = "/simpletemplate")
    public void handleSimpleEmailFromTemplate() {
        emailService.sendSimpleMessageFromSimpleTemplate(toAddress,
                "Simple Email using a template",
                "This is a test message sent from Spring.");
    }

    @PostMapping(path = "/simpleattachment")
    public void handleSimpleEmailWithAttachment() {
        emailService.sendMessageWithAttachment(toAddress,
                "Simple Email with an attachment",
                "This is a test message with an attachment sent from Spring.",
                attachmentPath);
    }

    @PostMapping(path = "/htmlattachment")
    public void handleHtmlEmailWithAttachment() {
        emailService.sendHtmlMessageWithAttachment(toAddress,
                "HTML Email with an attachment",
                attachmentPath);
    }
}
