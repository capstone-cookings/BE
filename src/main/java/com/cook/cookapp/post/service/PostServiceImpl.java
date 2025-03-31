package com.cook.cookapp.post.service;


import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.post.converter.PostConverter;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.repository.PostRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostConverter postConverter;

    @Override
    public void addPost(Long userId, PostDtoReq postDtoReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        postRepository.save(postConverter.toEntity(postDtoReq,user));
    }

    @Override
    public Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                    .map(postConverter::toDto);
    }

    @Override
    public PostResDto.SpecPostRes getPostById(Long postId,Long userId){
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return postConverter.toSpecDto(post);
    }

    @Override
    public void updatePost(Long postId, Long userId, PostDtoReq postDtoReq){
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        post.update(postDtoReq);
    }

    @Override
    public void deletePost(Long postId, Long userId){
        Post post = postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        postRepository.delete(post);
    }

}
