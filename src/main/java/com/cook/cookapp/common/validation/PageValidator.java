package com.cook.cookapp.common.validation;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageValidator implements ConstraintValidator<ValidPage, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null || value <= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("페이지 번호는 1 이상의 값이어야 합니다.")
                    .addConstraintViolation();
            return false; // 예외를 던지는 대신 `false` 반환하여 기본 검증 오류 메시지 제공
        }
        return true;
    }
}
