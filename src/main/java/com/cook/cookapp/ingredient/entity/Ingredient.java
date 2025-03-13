package com.cook.cookapp.ingredient.entity;


import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


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
    public void update(IngredientDtoReq ingredientDtoReq) {
        this.foodName = ingredientDtoReq.getFoodName();
        this.useByDate = ingredientDtoReq.getUseByDate();
        this.count = ingredientDtoReq.getCount();
        this.storageType = ingredientDtoReq.getStorageType();
        this.alarmStatus = ingredientDtoReq.getAlarmStatus();
    }

    // 알림 상태 변경 메서드
    public void setAlarmStatus(boolean alarmStatus) {
        this.alarmStatus = alarmStatus ? AlarmStatus.ON : AlarmStatus.OFF;
    }
}
