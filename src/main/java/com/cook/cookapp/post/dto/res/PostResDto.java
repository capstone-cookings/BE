package com.cook.cookapp.post.dto.res;

import com.cook.cookapp.post.entity.Enum.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class UserPostRes {
        private Long id;
//        private Category category;
        private int memberCount;
        private int likeCount;
        private int price;
        private String title;
//        private String content;
        private String timeAgo;
//        private LocalDateTime createdAt;
        private String district;
        private String neighborhood;
        private List<String> imageUrls;
        private boolean liked;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class SpecPostRes {
        private Long id;
        private Category category;
        private int memberCount;
        private int likeCount;
        private int price;
        private String title;
        private String content;
        private String timeAgo;
        private LocalDateTime createdAt;
        private String nickname;
        private String district;
        private String neighborhood;
        private boolean liked;
        private List<String> imageUrls;
        private List<Long> imageIds;
        private String profileImageUrl;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class likedRes{
        private boolean liked;
    }


}
