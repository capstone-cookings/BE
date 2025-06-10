package com.cook.cookapp.ingredient.entity;

import com.cook.cookapp.ingredient.entity.Enum.NotificationStatus;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.cook.cookapp.ingredient.entity.Enum.NotificationStatus.PENDING;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class IngredientNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private String content;

    private LocalDateTime scheduledAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = PENDING;

    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = PENDING;
        }
    }
}
