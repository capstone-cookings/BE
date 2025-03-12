package com.cook.cookapp.ingredient.entity;


import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
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

    //알림 상태 (ON,OFF)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmStatus alarmStatus = AlarmStatus.ON;

    @PrePersist
    public void prePersist() {
        if (this.alarmStatus == null) {
            this.alarmStatus = AlarmStatus.ON;
        }
    }
}
