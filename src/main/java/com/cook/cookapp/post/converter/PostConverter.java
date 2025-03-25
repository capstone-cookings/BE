package com.cook.cookapp.post.converter;

import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.recipe.dto.res.RecipeResDto;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

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

    public PostResDto.UserPostRes toDto(Post post) {
        return PostResDto.UserPostRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .price(post.getPrice())
                .content(post.getContent())
                .like_count(post.getLike_count())
                .category(post.getCategory())
                .build();
    }

}
