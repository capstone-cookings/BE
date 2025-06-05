package com.cook.cookapp.ingredient.entity.Enum;

public enum NotificationStatus {
    PENDING,   // 아직 처리되지 않음
    SENT,      // FCM 발송 완료
    SKIPPED    // 알림 OFF 등으로 전송 생략
}
