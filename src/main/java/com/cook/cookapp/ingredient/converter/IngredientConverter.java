package com.cook.cookapp.ingredient.converter;

import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RequiredArgsConstructor
@Component
public class IngredientConverter {
    private final AmazonS3Util amazonS3Util;
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
        String imageUrl = amazonS3Util.getIngredientPath(ingredient.getId());

        return IngredientDtoRes.builder()
                .id(ingredient.getId())
                .foodName(ingredient.getFoodName())
                .useByDate(ingredient.getUseByDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli())  // LocalDate → 밀리초 변환
                .count(ingredient.getCount())
                .imageUrl(imageUrl)
                .storageType(ingredient.getStorageType() == StorageType.FROZEN)
                .alarmStatus(ingredient.getAlarmStatus() == AlarmStatus.ON)
                .build();
    }
}
