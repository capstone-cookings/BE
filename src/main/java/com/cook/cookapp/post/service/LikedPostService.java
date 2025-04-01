package com.cook.cookapp.post.service;

import com.cook.cookapp.post.dto.res.PostResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikedPostService {
    PostResDto.likedRes likePost(Long userId, Long postId);
    Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable adjustedPageable);
}
