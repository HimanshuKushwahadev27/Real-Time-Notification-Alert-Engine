package com.emi.alertengine.sender;

import com.emi.alertengine.domain.NotificationChannel;
import com.emi.alertengine.domain.NotificationRequest;

public interface NotificationSender {

  
  void send(NotificationRequest request, String renderedContent);

  boolean supports(NotificationChannel channel);

  
}
