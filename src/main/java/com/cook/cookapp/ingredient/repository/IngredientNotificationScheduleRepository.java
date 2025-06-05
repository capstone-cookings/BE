package com.cook.cookapp.ingredient.repository;

import com.cook.cookapp.ingredient.entity.Enum.NotificationStatus;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.ingredient.entity.IngredientNotification;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IngredientNotificationScheduleRepository extends JpaRepository<IngredientNotification, Long> {
    boolean existsByIngredientAndScheduledAt(Ingredient ingredient, LocalDateTime scheduledAt);
    List<IngredientNotification> findByUserAndStatusOrderByScheduledAtDesc(User user, NotificationStatus status);
    List<IngredientNotification> findByStatusAndScheduledAtBefore(NotificationStatus status, LocalDateTime before);
    long countByUserIdAndIsReadFalseAndStatus(Long userId, NotificationStatus status);

}
