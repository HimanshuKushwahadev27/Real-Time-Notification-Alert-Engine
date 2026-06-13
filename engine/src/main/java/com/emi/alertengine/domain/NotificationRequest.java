package com.emi.alertengine.domain;

import java.util.Map;
import java.util.UUID;

public record NotificationRequest(
    UUID requestId,
    String recipientId,
    NotificationChannel channel,
    NotificationPriority priority,
    String templateId,
    Map<String, String> payload,
    NotificationPreference preference
) {}
