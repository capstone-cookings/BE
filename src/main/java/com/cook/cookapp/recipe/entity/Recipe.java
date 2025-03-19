package com.cook.cookapp.recipe.entity;

import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.user.entity.ProfileImage;
import com.cook.cookapp.user.entity.User;
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
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //레시피명
    @Column(length = 50, nullable = false)
    @NotNull
    private String title;

    //레시피 방법
    @Column(length = 500, unique = true)
    private String instructions;

    //재료들
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



}
