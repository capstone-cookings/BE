package com.cook.cookapp.user.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDtoReq {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginReq {
        @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TastePreferenceRequest {
        @Size(max = 50, message = "취향 입력은 최대 50자까지 가능합니다.")
        private String tastePreference;
    }

}
