package com.cook.cookapp.post.service;

import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    void addPost(Long userId, PostDtoReq postDtoReq);
    Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable pageable);
    PostResDto.SpecPostRes getPostById(Long postId,Long userId);
    void updatePost(Long postId, Long userId, PostDtoReq postDtoReq);
}
