package com.cook.cookapp.ingredient.dto.res;

import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import com.cook.cookapp.ingredient.entity.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Builder
public class IngredientDtoRes {
    private Long id;
    private String foodName;
    private Long useByDate;
    private int count;
    private boolean storageType;
    private boolean alarmStatus;

    public static IngredientDtoRes fromEntity(Ingredient ingredient) {
        return IngredientDtoRes.builder()
                .id(ingredient.getId())
                .foodName(ingredient.getFoodName())
                .useByDate(ingredient.getUseByDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli())  // LocalDate → 밀리초(Long) 변환
                .count(ingredient.getCount())
                .storageType(ingredient.getStorageType() == StorageType.FROZEN)  // 냉장(false) → 냉동(true)
                .alarmStatus(ingredient.getAlarmStatus() == AlarmStatus.ON)  // OFF(false), ON(true)
                .build();
    }
}
