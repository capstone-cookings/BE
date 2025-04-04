package com.cook.cookapp.user.entity;

import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.recipe.entity.Recipe;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 이름
    @Column(length = 20, nullable = false)
    @NotNull
    private String name;

    //사용자 닉네임
    @Column(length = 20, unique = true)
    private String nickname;

    //사용자 이메일
    @Column(length = 100, unique = true)
    private String email;

    //경험치
    @Column
    private Long exp;

    //음식 취향
    @Column(length = 100)
    private String tastePreference;

    //지역 구/군
    @Column(length = 20)
    private String district;

    //지역 동
    @Column(length = 20)
    private String neighborhood;

    //식재료 리스트
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Ingredient> IngredientList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Recipe> RecipeList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "profileImage_id")
    private ProfileImage profileImage;
}

