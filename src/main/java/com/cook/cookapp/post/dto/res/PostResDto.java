package com.cook.cookapp.post.dto.res;

import com.cook.cookapp.post.entity.Enum.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class PostResDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class UserPostRes {
        private Long id;
        private Category category;
        private int like_count;
        private int price;
        private String title;
        private String content;
    }
}
