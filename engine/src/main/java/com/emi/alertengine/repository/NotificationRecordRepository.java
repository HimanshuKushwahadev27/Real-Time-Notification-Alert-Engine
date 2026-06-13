package com.emi.alertengine.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emi.alertengine.domain.NotificationRecord;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, UUID> {
  
}
