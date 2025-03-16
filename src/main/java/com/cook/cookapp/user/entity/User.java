package com.cook.cookapp.user.entity;

import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.entity.Ingredient;
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

    //식재료 리스트
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Ingredient> IngredientList = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ProfileImage profileImage;

}
