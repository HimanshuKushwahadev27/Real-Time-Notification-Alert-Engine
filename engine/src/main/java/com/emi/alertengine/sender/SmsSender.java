package com.emi.alertengine.sender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.emi.alertengine.domain.NotificationChannel;
import com.emi.alertengine.domain.NotificationRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SmsSender implements NotificationSender {

  @Value("${twillio.account-sid}")
  private String accountSid;

  @Value("${twillio.auth-token}")
  private String authToken;

  @Value("${twillio.from-phone-number}")
  private String fromPhoneNumber;

  @PostConstruct
  public void init() {
    Twilio.init(accountSid, authToken);
    // Initialize Twilio client here with accountSid and authToken
    log.info("Initialized SmsSender with accountSid={}, fromPhoneNumber={}",
            accountSid, fromPhoneNumber);
  }
  @Override
  public void send(NotificationRequest request, String renderedContent) {
    Message message = Message.creator(
            new PhoneNumber(request.recipientId()),  // to
            new PhoneNumber(fromPhoneNumber),             // from
            renderedContent                             // body
    ).create();

    log.info("SMS sent to={} sid={} requestId={}",
                request.recipientId(), message.getSid(), request.requestId());
  }

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.SMS;
  }
  
}
