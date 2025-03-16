package com.cook.cookapp.recipe.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDtoReq {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginReq {
        String email;
    }
}
