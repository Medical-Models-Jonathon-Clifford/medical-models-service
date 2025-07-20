package org.jono.medicalmodelsservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailService {
  @Value("${spring.mail.username}")
  private String replyAddress;

  private final JavaMailSender emailSender;
  private final SimpleMailMessage template;

  @Autowired
  public EmailService(final JavaMailSender emailSender, final SimpleMailMessage template) {
    this.emailSender = emailSender;
    this.template = template;
  }

  public void sendSimpleMessage(final String to, final String subject, final String text) {
    final SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(replyAddress);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

  public void sendSimpleMessageFromSimpleTemplate(final String to, final String subject, final String text) {
    sendSimpleMessage(to, subject, String.format(Objects.requireNonNull(template.getText()), text));
  }

  public void sendMessageWithAttachment(final String to, final String subject, final String text,
                                        final String pathToAttachment) {
    try {
      final MimeMessage message = emailSender.createMimeMessage();
      final MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(replyAddress);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text);
      helper.addAttachment("50-note-1.jpg", new FileSystemResource(new File(pathToAttachment)));
      emailSender.send(message);
    } catch (MessagingException e) {
      log.error("Error sending simple email with attachment.", e);
    }
  }

  public void sendHtmlMessageWithAttachment(final String to, final String subject, final String pathToAttachment) {
    try {
      final MimeMessage message = emailSender.createMimeMessage();
      final MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(replyAddress);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText("<html><body>"
                         + "<h1>Very important news here</h1>"
                         + "<p>Look below for more information</p>"
                         + "<table><tr><th>Col 1</th><th>Col 2</th></tr><tr><td>Data 1</td><td>Data 2</td></tr></table>"
                         + "<img src='cid:identifier1234'>"
                         + "</body></html>", true);
      helper.addInline("identifier1234", new FileSystemResource(new File(pathToAttachment)));
      emailSender.send(message);
    } catch (MessagingException e) {
      log.error("Error sending HTML email with attachment.", e);
    }
  }
}
