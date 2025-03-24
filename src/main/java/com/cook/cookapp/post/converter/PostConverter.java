package com.cook.cookapp.post.converter;

import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Enum.StorageType;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class PostConverter {
    public Post toEntity(PostDtoReq dto, User user) {
        return Post.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .content(dto.getContent())
                .like_count(dto.getLike_count())
                .category(dto.getCategory())
                .user(user)
                .build();

    }

}
