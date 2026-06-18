package com.emi.alertengine.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emi.alertengine.domain.NotificationRecord;

import io.lettuce.core.dynamic.annotation.Param;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, UUID> {

  
  Optional<NotificationRecord> findByRequestId(@Param("requestId") UUID requestId);

  List<NotificationRecord> findByRecipientId(@Param("recipientId") String recipientId);
  
}
