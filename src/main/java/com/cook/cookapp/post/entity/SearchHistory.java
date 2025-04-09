package com.cook.cookapp.post.entity;

import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class SearchHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
