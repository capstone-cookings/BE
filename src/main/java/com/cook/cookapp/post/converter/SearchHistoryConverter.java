package com.cook.cookapp.post.converter;

import com.cook.cookapp.post.dto.res.PostResDto;
import com.cook.cookapp.post.dto.res.SearchResDto;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.entity.SearchHistory;
import org.springframework.stereotype.Component;

@Component
public class SearchHistoryConverter {
    public static SearchResDto.searchedRes toDto(SearchHistory searchHistory) {
        return SearchResDto.searchedRes.builder()
                .keyword(searchHistory.getKeyword())
                .build();
    }
}
