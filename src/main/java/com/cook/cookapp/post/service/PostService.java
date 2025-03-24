package com.cook.cookapp.post.service;

import com.cook.cookapp.post.dto.req.PostDtoReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    void addPost(Long userId, PostDtoReq postDtoReq);
}
