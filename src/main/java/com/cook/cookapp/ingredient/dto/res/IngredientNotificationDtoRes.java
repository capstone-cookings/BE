package com.cook.cookapp.ingredient.dto.res;

import com.cook.cookapp.ingredient.entity.Enum.NotificationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IngredientNotificationDtoRes {
    private Long id;
    private String ingredientName;
    private LocalDateTime scheduledAt;
    private NotificationStatus status;
    private boolean isRead;
}
