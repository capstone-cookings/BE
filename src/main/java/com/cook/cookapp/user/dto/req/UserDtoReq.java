package com.cook.cookapp.user.dto.req;

import com.cook.cookapp.recipe.entity.Recipe;
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeReq {
        Recipe recipe;
    }


}
