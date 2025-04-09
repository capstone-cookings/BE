package com.cook.cookapp.post.converter;

import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.post.dto.req.PostDtoReq;
import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.entity.PostImage;
import com.cook.cookapp.post.repository.LikedPostRepository;
import com.cook.cookapp.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PostConverter {
    private final AmazonS3Util amazonS3Util;
    private final LikedPostRepository likedPostRepository;

    public PostConverter(AmazonS3Util amazonS3Util, LikedPostRepository likedPostRepository) {
        this.amazonS3Util = amazonS3Util;
        this.likedPostRepository = likedPostRepository;
    }

    public Post toEntity(PostDtoReq dto, User user) {
        return Post.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .content(dto.getContent())
                .memberCount(dto.getMemberCount())
                .category(dto.getCategory())
                .user(user)
                .build();

    }

    public PostResDto.UserPostRes toDto(Post post,Long userId) {
        boolean isLiked = likedPostRepository.existsByUserIdAndPostId(userId,post.getId());
        PostResDto.UserPostRes userPostRes = PostResDto.UserPostRes.builder()
                .id(post.getId())
                .district(post.getUser().getDistrict())
                .neighborhood(post.getUser().getNeighborhood())
                .title(post.getTitle())
                .price(post.getPrice())
                .memberCount(post.getMemberCount())
                .timeAgo(calTime(post.getUpdatedAt()))
                .likeCount(post.getLikeCount())
                .liked(isLiked)
                .build();
        userPostRes.setImageUrls(amazonS3Util.getPostPath(post.getId()));
        return userPostRes;
    }

    public PostResDto.SpecPostRes toSpecDto(Post post, Long userId) {
        boolean isLiked = likedPostRepository.existsByUserIdAndPostId(userId, post.getId());

        List<String> imageUrls = amazonS3Util.getPostPath(post.getId());
        List<PostImage> postImages = post.getPostImage();

        List<PostResDto.SpecPostImageRes> imageList = new ArrayList<>();
        for (int i = 0; i < postImages.size(); i++) {
            imageList.add(PostResDto.SpecPostImageRes.builder()
                    .imageId(postImages.get(i).getId())
                    .imageUrl(imageUrls.get(i))
                    .build());
        }

        return PostResDto.SpecPostRes.builder()
                .id(post.getId())
                .nickname(post.getUser().getNickname())
                .district(post.getUser().getDistrict())
                .neighborhood(post.getUser().getNeighborhood())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .price(post.getPrice())
                .memberCount(post.getMemberCount())
                .timeAgo(calTime(post.getUpdatedAt()))
                .liked(isLiked)
                .likeCount(post.getLikeCount())
                .image(imageList)
                .profileImageUrl(amazonS3Util.getProfilePath(post.getUser().getId()))
                .build();
    }



    public static String calTime(LocalDateTime updatedAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(updatedAt, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "일 전";
        }else if(hours > 0) {
            return hours + "시간 전";
        }else if(minutes > 0) {
            return minutes + "분 전";
        }else {
            return "방금 전";
        }
    }

}
