package com.cook.cookapp.post.entity;


import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //식재료 이름
    @Column(length = 20, nullable = false)
    @NotNull
    private String foodName;

    //소비기한 (날짜만 사용)
    @Column
    private LocalDate useByDate;

    //수량
    @Column
    private int count;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
