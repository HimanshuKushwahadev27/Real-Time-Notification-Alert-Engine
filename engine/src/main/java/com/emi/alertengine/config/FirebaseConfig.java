package com.emi.alertengine.config;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FirebaseConfig {
  
  @Value("${firebase.service-account-path:firebase-service-account.json}")
  private String serviceAccountPath;

  @Bean
  public FirebaseMessaging firebaseMessaging() throws  java.io.IOException {
      if (FirebaseApp.getApps().isEmpty()) {
          InputStream serviceAccount =
                  new ClassPathResource(serviceAccountPath).getInputStream();

          GoogleCredentials credentials =
                  GoogleCredentials.fromStream(serviceAccount);

          FirebaseOptions options = FirebaseOptions.builder()
                  .setCredentials(credentials)
                  .build();

          FirebaseApp.initializeApp(options);
          log.info("Firebase initialized successfully");
      }

      return FirebaseMessaging.getInstance();
  }


}
