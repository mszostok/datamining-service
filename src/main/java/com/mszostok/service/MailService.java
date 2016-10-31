package com.mszostok.service;

import com.mszostok.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.concurrent.Future;

/**
 * Service for sending e-mails.
 */
@Slf4j
@Service("mailService")
public class MailService {

  @Autowired
  private JavaMailSenderImpl javaMailSender;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private SpringTemplateEngine templateEngine;

  @Async // indicating it will run on a separate thread.
  private void sendEmail(final String to, final String subject, final String content, final boolean isMultipart,
                         final boolean isHtml) {
    if (log.isDebugEnabled()) {
      log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
          isMultipart, isHtml, to, subject, content);
    }

    // Prepare message using a Spring helper
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, "UTF-8");
      message.setTo(to);
      message.setSubject(subject);
      message.setText(content, isHtml);
      javaMailSender.send(mimeMessage);

      log.info("Sent e-mail to User '{}'", to);
    } catch (Exception ex) {
      log.error("E-mail could not be sent to user '{}': {}", to, ex.getMessage());
    }
  }

  @Async
  public Future<String> sendActivationEmail(final User user, final String baseUrl) {
    log.info("Sending activation e-mail to '{}'", user.getEmail());

    Locale locale = Locale.US;
    Context context = new Context(locale);
    context.setVariable("user", user);
    context.setVariable("baseUrl", baseUrl);
    String content = templateEngine.process("activationEmail", context);
    String subject = messageSource.getMessage("email.activation.title", null, locale);

    sendEmail(user.getEmail(), subject, content, false, true);
    return new AsyncResult<>("email_sent");
  }
}
