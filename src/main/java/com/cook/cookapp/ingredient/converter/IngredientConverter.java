package com.cook.cookapp.ingredient.converter;

import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class IngredientConverter {
    public Ingredient toEntity(IngredientDtoReq dto, User user) {
        return Ingredient.builder()
                .foodName(dto.getFoodName())
                .useByDate(dto.getUseByDate())
                .count(dto.getCount())
                .storageType(dto.getStorageType())
                .alarmStatus(dto.getAlarmStatus())
                .user(user)
                .build();
    }

    public IngredientDtoRes toDto(Ingredient ingredient) {
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
