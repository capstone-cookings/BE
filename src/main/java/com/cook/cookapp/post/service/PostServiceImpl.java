package com.cook.cookapp.post.service;


import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.post.converter.PostConverter;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.LikedPost;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.entity.SearchHistory;
import com.cook.cookapp.post.repository.LikedPostRepository;
import com.cook.cookapp.post.repository.PostRepository;
import com.cook.cookapp.post.repository.SearchHistoryRepository;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.cook.cookapp.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND;


@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostConverter postConverter;
    private final AmazonS3Util amazonS3Util;
    private final LikedPostRepository likedPostRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Override
    public Long addPost(Long userId, PostDtoReq postDtoReq, List<MultipartFile> postImages) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Post post = postRepository.save(postConverter.toEntity(postDtoReq, user));

        if (postImages != null && !postImages.isEmpty()) {
            amazonS3Util.uploadPostImages(postImages, post);  // 기존 postImageUpload에서 remainImageUrls 없는 버전
        }

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
        saveSearchHistory(userId, keyword);
        Page<Post> post = postRepository.findByTitleContaining(keyword, pageable);
        return post.map(p -> postConverter.toDto(p,userId));
    }

    public void saveSearchHistory(Long userId, String keyword){
        if (keyword == null || keyword.trim().isEmpty()) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(USER_NOT_FOUND));

        // 이미 같은 검색어가 있으면 시간만 갱신
        Optional<SearchHistory> existing = searchHistoryRepository.findByUserAndKeyword(user, keyword);
        if (existing.isPresent()) {
            existing.get().setSearchedAt(LocalDateTime.now());
        } else {
            SearchHistory newRecord = SearchHistory.builder()
                    .keyword(keyword)
                    .searchedAt(LocalDateTime.now())
                    .user(user)
                    .build();
            searchHistoryRepository.save(newRecord);
        }
    }

}
