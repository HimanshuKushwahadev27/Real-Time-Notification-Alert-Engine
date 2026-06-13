package com.emi.alertengine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "notification_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID requestId;

    private String recipientId;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String templateId;

    private int retryCount;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "TEXT")        
    private Map<String, String> payload;

    private LocalDateTime sentAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    public static NotificationRecord from(NotificationRequest request) {
        return NotificationRecord.builder()
                .requestId(request.requestId())
                .recipientId(request.recipientId())
                .channel(request.channel())
                .priority(request.priority())
                .templateId(request.templateId())
                .payload(request.payload())
                .status(NotificationStatus.PENDING)
                .build();
    }

    public void markSent() {
      this.status = NotificationStatus.SENT;
    }

    public void markFailed(String message) {
      this.status = NotificationStatus.FAILED;
    }
}
