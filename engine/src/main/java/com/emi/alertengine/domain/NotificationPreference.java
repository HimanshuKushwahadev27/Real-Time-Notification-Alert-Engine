package com.emi.alertengine.domain;

public record NotificationPreference(
     boolean channelEnabled,
    boolean inQuietHours
) {

}
