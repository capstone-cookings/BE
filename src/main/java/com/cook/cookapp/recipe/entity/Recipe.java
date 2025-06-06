package com.cook.cookapp.recipe.entity;

import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
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

    @Column
    private boolean isLiked = false;

    //레시피 방법
    @Column(length = 500)
    private String instructions;

    //재료들
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "recipeImage_id")
    private RecipeImage recipeImage;

    public void update(RecipeDtoReq.RecipeReq recipeReq) {
        this.title = recipeReq.getTitle();
        this.instructions = recipeReq.getInstructions();
    }
}
