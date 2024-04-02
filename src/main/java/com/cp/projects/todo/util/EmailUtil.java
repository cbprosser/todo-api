package com.cp.projects.todo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.cp.projects.todo.config.EmailConfig;
import com.cp.projects.todo.model.table.VerificationToken;

import lombok.Builder;
import lombok.Data;

@Component
public class EmailUtil {

  private static JavaMailSender mailSender;
  private static EmailConfig emailConfig;

  @Autowired
  private void init(JavaMailSender mailSender) {
    EmailUtil.mailSender = mailSender;
    EmailUtil.emailConfig = new EmailConfig();
  }

  private static void sendEmail(EmailOptions opts) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(opts.to);
    msg.setFrom(opts.from);
    msg.setSubject(opts.subject);
    msg.setText(opts.bodyText);

    mailSender.send(msg);
  }

  public static void sendConfirmUserEmail(String to, String username, VerificationToken token) {
    sendEmail(EmailOptions.builder()
        // .to(to)
        .to(emailConfig.getEmail())
        .from(emailConfig.getEmail())
        .subject("ToDoLoo new user confirmation")
        .bodyText(String.format(
            "Hi %s!\n\nThank you for signing up for ToDoLoo! To confirm your email, click this link:\n\n%s",
            username,
            "http://localhost:5173/register/" + token.getToken()))
        .build());
  }

  public static void sendRecoveryEmail(VerificationToken token) {
    sendEmail(EmailOptions.builder()
        // .to(token.getUser.getEmail())
        .to(emailConfig.getEmail())
        .from(emailConfig.getEmail())
        .subject("ToDoLoo recovery confirmation")
        .bodyText(String.format(
            "Hi %s\n\nA request was recently made to recover your account. If this was made in error, please disregard this email. Otherwise, click this link to reset your password:\n\n%s",
            token.getUser().getUsername(),
            "http://localhost:5173/forgot/" + token.getToken()))
        .build());
  }

  @Data
  @Builder(toBuilder = true)
  private static class EmailOptions {
    String to;
    String from;
    String subject;
    String bodyText;
  }

}
