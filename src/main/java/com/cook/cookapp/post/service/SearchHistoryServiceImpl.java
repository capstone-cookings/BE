package com.cook.cookapp.post.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.post.converter.SearchHistoryConverter;
import com.cook.cookapp.post.dto.res.SearchResDto;
import com.cook.cookapp.post.entity.SearchHistory;
import com.cook.cookapp.post.repository.SearchHistoryRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class SearchHistoryServiceImpl implements SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    public List<SearchResDto.searchedRes> getSearchHistory(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<SearchHistory> searchHistoryList = searchHistoryRepository.findTop10ByUserOrderBySearchedAtDesc(user);
        List<SearchResDto.searchedRes> searchedResList = searchHistoryList.stream()
                .map(SearchHistoryConverter::toDto)
                .collect(Collectors.toList());
        return searchedResList;
    }

    public void deleteSearchHistory(Long userId, String keyword){
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        SearchHistory searchHistory = searchHistoryRepository.findByUserAndKeyword(user, keyword).orElseThrow(() -> new GeneralException(ErrorStatus.NO_EXISTS_USER_SEARCH_HISTORY));

        searchHistoryRepository.delete(searchHistory);
        searchHistoryRepository.flush();
    }

    public void deleteAllSearchHistory(Long userId){
        searchHistoryRepository.deleteAllByUserId(userId);
        searchHistoryRepository.flush();
    }
}
