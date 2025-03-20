package com.cook.cookapp.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAccessTokenRequest {
    @NotBlank(message = "엑세스 토큰은 필수입니다.")
    private String accessToken;
}