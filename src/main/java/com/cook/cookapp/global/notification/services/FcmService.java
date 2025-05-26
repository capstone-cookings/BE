package com.cook.cookapp.global.notification.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    public void sendFcm(String targetToken, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            FirebaseMessaging.getInstance().sendAsync(message);
            log.info("[FCM] 푸시 알림 전송 완료 - 대상: {}", targetToken);
        } catch (Exception e) {
            log.error("[FCM] 푸시 알림 전송 실패", e);
        }
    }
}
