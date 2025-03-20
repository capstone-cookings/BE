package com.cook.cookapp.ingredient.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IngredientDtoReq {
    @NotBlank(message = "식재료 이름은 필수입니다.")
    private String foodName;
    private Long useByDate;
    @Positive(message = "수량은 양수여야 합니다.")
    private int count;
    @NotNull(message = "저장 유형은 필수입니다.")
    private boolean storageType;
    @NotNull(message = "알림 상태는 필수입니다.")
    private boolean alarmStatus;
}