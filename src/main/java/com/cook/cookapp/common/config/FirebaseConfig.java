package com.cook.cookapp.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options;

            String firebaseKeyJson = System.getenv("FIREBASE_KEY_JSON");

            if (firebaseKeyJson != null && !firebaseKeyJson.isEmpty()) {
                log.info("[Firebase] 환경변수 기반 초기화");
                InputStream serviceAccount = new ByteArrayInputStream(firebaseKeyJson.getBytes(StandardCharsets.UTF_8));
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            } else {
                log.info("[Firebase] 파일 기반 초기화 - 경로: {}", firebaseConfigPath);
                FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("[Firebase] FirebaseApp 초기화 완료");
            }
        } catch (IOException e) {
            log.error("[Firebase] FirebaseApp 초기화 실패", e);
        }
    }
}