package com.cook.cookapp.post.service;


import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.post.converter.PostConverter;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.LikedPost;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.repository.LikedPostRepository;
import com.cook.cookapp.post.repository.PostRepository;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostConverter postConverter;
    private final AmazonS3Util amazonS3Util;
    private final LikedPostRepository likedPostRepository;

    @Override
    public Long addPost(Long userId, PostDtoReq postDtoReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Post post = postRepository.save(postConverter.toEntity(postDtoReq, user));

        return post.getId();
    }

    //내가 쓴 글 조회
    @Override
    public Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable pageable) {
        Page<Post> post = postRepository.findByUserId(userId, pageable);
        return post.map(p -> postConverter.toDto(p,userId));

    }

    @Override
    public PostResDto.SpecPostRes getPostById(Long postId,Long userId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return postConverter.toSpecDto(post,userId);
    }

    @Override
    public void updatePost(Long postId, Long userId, PostDtoReq postDtoReq){
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        post.update(postDtoReq);
    }

    @Override
    public void deletePost(Long postId, Long userId){
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        List<String> imageUrls = amazonS3Util.getPostPath(postId);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            amazonS3Util.deletePostImages(imageUrls);
        }

        postRepository.delete(post);
    }

    @Override
    public Page<PostResDto.UserPostRes> searchPosts(Long userId, String keyword, Pageable pageable){
        Page<Post> post = postRepository.findByTitleContaining(keyword, pageable);
        return post.map(p -> postConverter.toDto(p,userId));
    }

}
