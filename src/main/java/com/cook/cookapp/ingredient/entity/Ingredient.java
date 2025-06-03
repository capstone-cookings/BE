package com.cook.cookapp.ingredient.entity;


import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.dto.req.IngredientUpdateDtoReq;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //식재료 이름
    @Column(length = 20, nullable = false)
    @NotNull
    private String foodName;

    //소비기한 (날짜만 사용)
    @Column
    private LocalDate useByDate;

    //수량
    @Column
    private int count;

    //알림 상태 (ON,OFF) - 기본값 ON.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmStatus alarmStatus = AlarmStatus.ON;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "ingredient_id")
    private IngredientImage ingredientImage;

    // 저장 타입 (냉장, 냉동) - 기본값 냉장.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageType storageType = StorageType.REFRIGERATED;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.alarmStatus == null) {
            this.alarmStatus = AlarmStatus.ON;
        }
        if (this.storageType == null) {
            this.storageType = StorageType.REFRIGERATED;
        }
    }

    // 식재료 정보 업데이트 메서드
    public void update(IngredientUpdateDtoReq ingredientDtoReq) {
        this.foodName = ingredientDtoReq.getFoodName();
        this.useByDate =  Instant.ofEpochMilli(ingredientDtoReq.getUseByDate())
                .atZone(ZoneId.systemDefault()) // 밀리초(Long) → LocalDate 변환
                .toLocalDate();
        this.count = ingredientDtoReq.getCount();
        this.storageType = ingredientDtoReq.isStorageType() ? StorageType.FROZEN : StorageType.REFRIGERATED;
        this.alarmStatus = ingredientDtoReq.isAlarmStatus() ? AlarmStatus.ON : AlarmStatus.OFF;
    }
}
