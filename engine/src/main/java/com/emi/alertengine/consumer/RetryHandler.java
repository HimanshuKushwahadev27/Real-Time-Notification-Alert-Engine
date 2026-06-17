package com.emi.alertengine.consumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.emi.alertengine.domain.NotificationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryHandler {

  private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    @Value("${notification.topics.retry}")
    private String retryTopic;

    @Value("${notification.topics.dlq}")
    private String dlqTopic;

    @Value("${notification.retry.max-attempts}")
    private int maxRetryAttempts;

    //called by the notificationConsumer when dispatch fails. It decides whether to retry or send to DLQ based on the number of attempts.
    //logic  retryCount < maxRetryAttempts → publish to retry topic
     // retryCount >= maxRetryAttempts → publish to DLQ, give up
    //note we will republish to kafka instead of in place as using in place will use thread.sleep and loop which will block other messages in kafka consumer thread.

    public void handle(NotificationRequest request, Exception cause) {
        int currentRetryCount = request.retryCount();

        if (currentRetryCount < maxRetryAttempts) {
            log.warn("Scheduling retry {}/{} for requestId={}",
                    currentRetryCount + 1, maxRetryAttempts, request.requestId());
            publishToRetryTopic(request, currentRetryCount + 1);
        } else {
            log.error("Max retries exhausted for requestId={}. Sending to DLQ. Reason={}",
                    request.requestId(), cause.getMessage());
            publishToDlq(request, cause);
        }
    }

    private void publishToRetryTopic(NotificationRequest request, int newRetryCount) {
        
         //NotificationRequest is a record (immutable) — we create a new instance
         // with incremented retryCount so the retry consumer knows how many
         //attempts have already been made.
         
        NotificationRequest retryRequest = new NotificationRequest(
                request.requestId(),
                request.recipientId(),
                request.channel(),
                request.priority(),
                request.templateId(),
                request.payload(),
                request.preference(),
                newRetryCount
        );

        kafkaTemplate.send(retryTopic, request.requestId().toString(), retryRequest);
        log.info("Published to retry topic | requestId={} retryCount={}",
                request.requestId(), newRetryCount);
    }

    private void publishToDlq(NotificationRequest request, Exception cause) {
        kafkaTemplate.send(dlqTopic, request.requestId().toString(), request);
        log.error("Published to DLQ | requestId={} cause={}",
                request.requestId(), cause.getMessage());
    }
  
}
