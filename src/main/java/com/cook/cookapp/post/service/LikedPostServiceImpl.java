package com.cook.cookapp.post.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chat.entity.ChatRoom;
import com.cook.cookapp.chat.repository.ChatRoomRepository;
import com.cook.cookapp.post.converter.PostConverter;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.LikedPost;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.repository.LikedPostRepository;
import com.cook.cookapp.post.repository.PostRepository;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class LikedPostServiceImpl implements LikedPostService {

    private final LikedPostRepository likedPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostConverter postConverter;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public PostResDto.likedRes likePost(Long userId, Long postId) {
        boolean exists = likedPostRepository.existsByUserIdAndPostId(userId, postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        if (!exists) {
            LikedPost likedPost = LikedPost.builder()
                    .user(userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND)))
                    .post(post)
                    .build();
            // 좋아요가 없다면 새로 추가
            likedPostRepository.save(likedPost);
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            LikedPost likedPost = likedPostRepository.findByUserIdAndPostId(userId, postId).orElseThrow(() -> new GeneralException(ErrorStatus.LIKED_POST_NOT_FOUND));
            // 이미 좋아요가 있으면, 해당 좋아요를 삭제하거나 카운트를 조정하는 로직
            likedPostRepository.delete(likedPost);
            likedPostRepository.flush();
            post.setLikeCount(post.getLikeCount() - 1);
        }

        postRepository.save(post);
        return PostResDto.likedRes.builder()
                .liked(likedPostRepository.existsByUserIdAndPostId(userId,postId))
                .build();
    }

    public Page<PostResDto.UserPostRes> findByUserId(Long userId, Pageable pageable) {
        return likedPostRepository.findByUserId(userId, pageable)
                .map(likedPost -> {
                    Post post = likedPost.getPost();
                    ChatRoom chatRoom = chatRoomRepository.findByPostId(post.getId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.CHATROOM_NOT_FOUND));
                    return postConverter.toDto(post, userId, chatRoom);
                });
    }
}
