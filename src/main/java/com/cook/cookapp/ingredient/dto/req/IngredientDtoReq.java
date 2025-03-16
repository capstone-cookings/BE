package com.cook.cookapp.ingredient.dto.req;

import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class IngredientDtoReq {
    private String foodName;
    private Long useByDate;
    private int count;
    private boolean storageType;
    private boolean alarmStatus;
}