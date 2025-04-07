package com.cook.cookapp.post.service;

import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    Long addPost(Long userId, PostDtoReq postDtoReq, List<MultipartFile> postImages) throws IOException;
    Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable pageable);
    PostResDto.SpecPostRes getPostById(Long postId,Long userId);
    void updatePost(Long postId, Long userId, PostDtoReq postDtoReq);
    void deletePost(Long postId, Long userId);
    Page<PostResDto.UserPostRes> searchPosts(Long userId, String keyword, Pageable pageable);
}
