package com.emi.alertengine.service;

import org.springframework.stereotype.Service;

import com.emi.alertengine.domain.NotificationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Service
public class DeduplicationService {
  

public boolean isDuplicate(NotificationRequest request) {
  // TODO Auto-generated method stub
  throw new UnsupportedOperationException("Unimplemented method 'isDuplicate'");
}

public void markSent(NotificationRequest request) {
  // TODO Auto-generated method stub
  throw new UnsupportedOperationException("Unimplemented method 'markSent'");
}
  

  
}
