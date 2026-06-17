package com.emi.alertengine.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.emi.alertengine.domain.NotificationRequest;
import com.emi.alertengine.service.DispatcherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
  
  private final DispatcherService dispatcherService;
  private final RetryHandler retryHandler;


  //we use manual acknowledgment to ensure that we only commit the offset after successful processing of the message. This is crucial for ensuring that we don't lose messages in case of failures.


  @KafkaListener(
    topics = "notification.events",
    groupId = "notification-engine",
    containerFactory = "kafkaListenerContainerFactory"
  )
  public void consume(
    @Payload NotificationRequest request,
    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
    @Header(KafkaHeaders.OFFSET) long offset,
    Acknowledgment ack
  ){
      log.info("Received notification event | requestId={} channel={} partition={} offset={}",
                request.requestId(), request.channel(), partition, offset);
    
       try {
            dispatcherService.dispatch(request);

            // Commit offset only after successful dispatch
            ack.acknowledge();

            log.info("Offset committed | requestId={} offset={}", request.requestId(), offset);

        } catch (Exception e) {
            log.error("Dispatch failed | requestId={} reason={}", request.requestId(), e.getMessage());

            // Hand off to RetryHandler — it decides whether to retry or send to DLQ
            retryHandler.handle(request, e);

            // Acknowledge even on failure — RetryHandler republishes to retry topic
            // so we dont want Kafka to re-deliver the same message endlessly
            ack.acknowledge();
        }

  }
}
