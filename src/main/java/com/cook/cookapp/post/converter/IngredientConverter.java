package com.cook.cookapp.post.converter;

import com.cook.cookapp.post.dto.req.IngredientDtoReq;
import com.cook.cookapp.post.dto.res.IngredientDtoRes;
import com.cook.cookapp.post.entity.Enum.AlarmStatus;
import com.cook.cookapp.post.entity.Enum.StorageType;
import com.cook.cookapp.post.entity.Ingredient;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class IngredientConverter {
    public Ingredient toEntity(IngredientDtoReq dto, User user) {
        return Ingredient.builder()
                .foodName(dto.getFoodName())
                .useByDate(Instant.ofEpochMilli(dto.getUseByDate()) // 밀리초 → LocalDate 변환
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .count(dto.getCount())
                .storageType(dto.isStorageType() ? StorageType.FROZEN : StorageType.REFRIGERATED)
                .alarmStatus(dto.isAlarmStatus() ? AlarmStatus.ON : AlarmStatus.OFF)
                .user(user)
                .build();
    }

    public IngredientDtoRes toDto(Ingredient ingredient) {
        return IngredientDtoRes.builder()
                .id(ingredient.getId())
                .foodName(ingredient.getFoodName())
                .useByDate(ingredient.getUseByDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli())  // LocalDate → 밀리초 변환
                .count(ingredient.getCount())
                .storageType(ingredient.getStorageType() == StorageType.FROZEN)
                .alarmStatus(ingredient.getAlarmStatus() == AlarmStatus.ON)
                .build();
    }
}
