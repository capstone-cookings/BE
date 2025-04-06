package com.cook.cookapp.post.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class SearchResDto {
    @Data
    @AllArgsConstructor
    @Builder
    public static class searchedRes{
        private String keyword;
    }
}
