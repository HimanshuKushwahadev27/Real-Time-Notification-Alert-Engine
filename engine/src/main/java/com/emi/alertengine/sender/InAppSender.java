package com.emi.alertengine.sender;

import org.springframework.stereotype.Component;

import com.emi.alertengine.domain.NotificationChannel;
import com.emi.alertengine.domain.NotificationRequest;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InAppSender implements NotificationSender {

  private final SimpMessagingTemplate messagingTemplate; // For WebSocket notifications

  @Override
  public void send(NotificationRequest request, String renderedContent) {
    Map<String, String> wsPayload = Map.of(
        "requestId",  request.requestId().toString(),
        "templateId", request.templateId(),
        "body",       renderedContent,
        "priority",   request.priority().name()
    );

    messagingTemplate.convertAndSendToUser(request.recipientId(),"/queue/notifications", wsPayload);

    log.info("In-app notification sent to={} requestId={}",
                request.recipientId(), request.requestId());
  }

  @Override
  public boolean supports(NotificationChannel channel) {
    return channel == NotificationChannel.IN_APP;
  }
  
}
