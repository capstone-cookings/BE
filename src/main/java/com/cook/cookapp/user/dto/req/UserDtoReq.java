package com.cook.cookapp.user.dto.req;

import com.cook.cookapp.recipe.entity.Recipe;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDtoReq {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginReq {
        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;
    }

}
