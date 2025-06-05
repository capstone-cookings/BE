package com.cook.cookapp.ingredient.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientDtoRes {
    private Long id;
    private String foodName;
    private String imageUrl;
    private Long useByDate;
    private int count;
    private boolean storageType;
    private boolean alarmStatus;
}
