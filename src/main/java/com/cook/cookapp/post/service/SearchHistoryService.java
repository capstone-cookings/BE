package com.cook.cookapp.post.service;

import com.cook.cookapp.post.dto.res.SearchResDto;

import java.util.List;

public interface SearchHistoryService {
    List<SearchResDto.searchedRes> getSearchHistory(Long userId);
    void deleteSearchHistory(Long userId, String keyword);
    void deleteAllSearchHistory(Long userId);
}
