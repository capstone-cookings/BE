package com.cook.cookapp.ingredient.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.global.notification.services.FcmService;
import com.cook.cookapp.ingredient.dto.res.IngredientNotificationDtoRes;
import com.cook.cookapp.ingredient.entity.Enum.NotificationStatus;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.ingredient.entity.IngredientNotification;
import com.cook.cookapp.ingredient.repository.IngredientNotificationScheduleRepository;
import com.cook.cookapp.ingredient.repository.IngredientRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import com.cook.cookapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientNotificationService {
    private final IngredientNotificationScheduleRepository notificationRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final FcmService fcmService;

    public void scheduleNotification(Long userId, Long ingredientId, LocalDateTime whenToNotify) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        var ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INGREDIENT_NOT_FOUND));

        if (!ingredient.isNotificationEnabled()) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_DISABLED);
        }

        if (whenToNotify.isAfter(ingredient.getUseByDate().atStartOfDay())) {
            throw new GeneralException(ErrorStatus.INVALID_NOTIFICATION_TIME);
        }

        if (whenToNotify.isBefore(LocalDateTime.now())) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_TIME_IN_PAST);
        }

        boolean exists = notificationRepository.existsByIngredientAndScheduledAt(ingredient, whenToNotify);
        if (exists) {
            throw new GeneralException(ErrorStatus.ALREADY_RESERVED);
        }

        var schedule = IngredientNotification.builder()
                .user(user)
                .ingredient(ingredient)
                .scheduledAt(whenToNotify)
                .status(NotificationStatus.PENDING)
                .build();

        notificationRepository.save(schedule);
    }


    @Scheduled(fixedRate = 60000)
    public void sendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();
        var schedules = notificationRepository.findByStatusAndScheduledAtBefore(NotificationStatus.PENDING, now);

        for (var schedule : schedules) {
            Ingredient ingredient = schedule.getIngredient();

            if (!ingredient.isNotificationEnabled()) {
                schedule.setStatus(NotificationStatus.SKIPPED);
                continue;
            }

            String token = userService.getFcmToken(schedule.getUser().getId());
            fcmService.sendFcmIngredient(token, "소비기한 알림", ingredient.getFoodName() + " 소비기한이 임박합니다!");
            schedule.setStatus(NotificationStatus.SENT);
        }
    }

    @Transactional(readOnly = true)
    public List<IngredientNotificationDtoRes> getMyNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<IngredientNotification> list = notificationRepository.findByUserAndStatusOrderByScheduledAtDesc(user, NotificationStatus.SENT);

        return list.stream().map(n -> IngredientNotificationDtoRes.builder()
                .id(n.getId())
                .ingredientName(n.getIngredient().getFoodName())
                .scheduledAt(n.getScheduledAt())
                .status(n.getStatus())
                .isRead(n.isRead())
                .build()
        ).toList();
    }

    public void markAsRead(Long userId, Long notificationId) {
        IngredientNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        notification.setRead(true);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalseAndStatus(userId,  NotificationStatus.SENT);
    }

}
