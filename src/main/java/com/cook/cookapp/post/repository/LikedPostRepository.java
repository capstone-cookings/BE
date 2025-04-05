package com.cook.cookapp.post.repository;

import com.cook.cookapp.post.converter.LikedPostConverter;
import com.cook.cookapp.post.entity.LikedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    Optional<LikedPost> findByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Page<LikedPost> findByUserId(Long userId, Pageable pageable);
    boolean existsByUserId(Long userId);
}
