package com.cook.cookapp.post.repository;

import com.cook.cookapp.post.dto.res.SearchResDto;
import com.cook.cookapp.post.entity.SearchHistory;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Optional<SearchHistory> findByUserId(Long userId);
    Optional<SearchHistory> findByUserAndKeyword(User user, String keyword);
    List<SearchHistory> findTop10ByUserOrderBySearchedAtDesc(User user);
    void deleteAllByUserId(Long userId);
}
