package com.cook.cookapp.ingredient.dto.res;

import com.cook.cookapp.ingredient.entity.Ingredient;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class IngredientDtoRes {
    private Long id;
    private String foodName;
    private LocalDate useByDate;
    private int count;
    private String storageType;
    private String alarmStatus;

    public static IngredientDtoRes fromEntity(Ingredient ingredient) {
        return IngredientDtoRes.builder()
                .id(ingredient.getId())
                .foodName(ingredient.getFoodName())
                .useByDate(ingredient.getUseByDate())
                .count(ingredient.getCount())
                .storageType(ingredient.getStorageType().name())
                .alarmStatus(ingredient.getAlarmStatus().name())
                .build();
    }
}
