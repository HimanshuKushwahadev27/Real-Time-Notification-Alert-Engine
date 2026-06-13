package com.emi.alertengine.sender;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.emi.alertengine.domain.NotificationChannel;
import com.emi.alertengine.domain.NotificationRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender implements NotificationSender {


  private final JavaMailSender mailSender; 

  //recipientId is email address in this case
  //renderedContent is the (html string from the temlate engine) content to be sent in the email body
  @Override
  public void send(NotificationRequest request, String renderedContent) {
    try {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setTo(request.recipientId());
    helper.setSubject(resolveSubject(request));
    helper.setText(renderedContent, true); // true = HTML body

    mailSender.send(message);

    log.info("Email sent to={} requestId={}",
            request.recipientId(), request.requestId());

    }catch (MessagingException e) {
    throw new RuntimeException(
            "EmailSender failed for requestId=" + request.requestId(), e);
    }
  }

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.EMAIL;
  }

  private String resolveSubject(NotificationRequest request) {
    if (request.payload() != null && request.payload().containsKey("subject")) {
        return request.payload().get("subject");
    }
    return "You have a new notification";
  }
  
}
