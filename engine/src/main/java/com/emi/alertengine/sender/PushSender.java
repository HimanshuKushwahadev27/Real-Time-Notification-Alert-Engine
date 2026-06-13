package com.emi.alertengine.sender;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.emi.alertengine.domain.NotificationChannel;
import com.emi.alertengine.domain.NotificationRequest;

@Slf4j
@Component
public class PushSender implements NotificationSender {

  @Override
  public void send(NotificationRequest request, String renderedContent) {
    try{
      Message fcmMessage = Message.builder()
                    .setToken(request.recipientId())
                    .setNotification(Notification.builder()
                            .setTitle(resolveTitle(request))
                            .setBody(renderedContent)
                            .build())
                    .putAllData(request.payload()) // passes payload as custom data to app
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(fcmMessage);

            log.info("Push sent to={} fcmMessageId={} requestId={}",
                    request.recipientId(), messageId, request.requestId());

    } catch (FirebaseMessagingException e) {
        throw new RuntimeException(
                "PushSender failed for requestId=" + request.requestId(), e);
    }
  }

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.PUSH;
  }

  private String resolveTitle(NotificationRequest request) {
    if (request.payload() != null && request.payload().containsKey("title")) {
        return request.payload().get("title");
    }
    return "New notification";
  }

}
