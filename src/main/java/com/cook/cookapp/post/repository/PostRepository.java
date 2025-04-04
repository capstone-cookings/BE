package com.cook.cookapp.post.repository;

import com.cook.cookapp.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);
    Optional<Post> findByIdAndUserId(Long postId, Long userId);
    @Query("SELECT p FROM Post p WHERE :keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword%")
    Page<Post> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);
}
