package com.emi.alertengine.domain;

import java.util.UUID;

public record NotificationResponse(

  UUID requestId,
  String Status,
  String Msg
) {
  
}
