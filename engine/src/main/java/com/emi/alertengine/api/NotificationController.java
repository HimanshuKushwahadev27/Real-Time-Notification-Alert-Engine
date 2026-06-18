package com.emi.alertengine.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emi.alertengine.domain.NotificationRecord;
import com.emi.alertengine.domain.NotificationRequest;
import com.emi.alertengine.domain.NotificationResponse;
import com.emi.alertengine.repository.NotificationRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notifications")
@RestController
public class NotificationController {
  
  private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
  private final NotificationRecordRepository notificationRepository;

  private static final String EVENTS_TOPICS = "notification.events";

  //pushed to kafka instead of calling directly so that the preceeding requests dont have to wait for the notification to be sent and also to decouple the notification sending logic from the main flow

  @PostMapping("/send")
  public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
    log.info("Received notification request: {}", request);
   
        kafkaTemplate.send(EVENTS_TOPICS, request.requestId().toString(), request);

        return ResponseEntity
                .accepted()
                .body(new NotificationResponse(
                        request.requestId(),
                        "ACCEPTED",
                        "Notification queued for delivery"
        ));
  }


  //caller polls delivery status of the notification
  @GetMapping("/{requestId}/status")
  public ResponseEntity<NotificationRecord> getStatus(@PathVariable UUID requestId) {
      return notificationRepository.findByRequestId(requestId)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
  }



  @GetMapping("/recipient/{recipientId}")
  public ResponseEntity<List<NotificationRecord>> getByRecipient(
          @PathVariable String recipientId) {
      List<NotificationRecord> records = notificationRepository.findByRecipientId(recipientId);
      return ResponseEntity.ok(records);
  }
}
