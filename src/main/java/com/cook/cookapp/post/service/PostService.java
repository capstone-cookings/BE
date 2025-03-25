package com.cook.cookapp.post.service;

import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    void addPost(Long userId, PostDtoReq postDtoReq);
    Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable pageable);
}
