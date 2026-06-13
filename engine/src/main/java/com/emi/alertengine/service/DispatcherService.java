package com.emi.alertengine.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.emi.alertengine.domain.NotificationChannel;
import com.emi.alertengine.domain.NotificationPriority;
import com.emi.alertengine.domain.NotificationRecord;
import com.emi.alertengine.domain.NotificationRequest;
import com.emi.alertengine.repository.NotificationRecordRepository;
import com.emi.alertengine.sender.NotificationSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class DispatcherService {
  
  private final DeduplicationService deduplicationService;
  private final List<NotificationSender> notificationSenders;
  private final NotificationRecordRepository notificationRecordRepository;
  private final TemplateEngine templateEngine;


  public void dispatch(NotificationRequest request){
    log.info("Dispatching requestId={} channel={} recipient={}" ,
             request.requestId(), request.channel(), request.recipientId());

    if(deduplicationService.isDuplicate(request)){
      log.info("Duplicate notification detected for requestId={}", request.requestId());
      return;
    }

    //persist record in case we need to retry later
    NotificationRecord record = NotificationRecord.from(request);
    notificationRecordRepository.save(record);

    try{
      //preferences check , this will be bypassed for critical notifications
      if(request.priority() != NotificationPriority.CRITICAL){
        if(request.preference().inQuietHours()){
          log.info("Recipient {} is in quiet hours, skipping non-critical notification", request.recipientId());
          return;
        }
        if(request.preference().channelEnabled()){
          log.info("Channel is disabled user doesnt wants the email  {}", request.recipientId());
          return;
        }
      }

      //render template 
      String renderedContent = templateEngine.render(request.templateId(), request.payload());

      //resolve the right sender based on channel
      NotificationSender sender = resolveSender(request.channel());
      sender.send(request, renderedContent);

      //mark success in record
      record.markSent();
      notificationRecordRepository.save(record);
      deduplicationService.markSent(request);

      log.info("Delivered. requestId={}", request.requestId());

    }catch(Exception ex){
      log.error("Delivery failed. requestId={} reason={}", request.requestId(), ex.getMessage());
      record.markFailed(ex.getMessage());
      notificationRecordRepository.save(record);
      throw ex;
    }
  }

  private NotificationSender resolveSender(NotificationChannel channel) {
    return notificationSenders.stream()
        .filter(sender -> sender.supports(channel))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No sender found for channel " + channel));
  }
}
