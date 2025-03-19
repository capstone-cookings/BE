package com.cook.cookapp.recipe.entity;

import com.cook.cookapp.global.BaseEntity;
import com.cook.cookapp.ingredient.entity.Ingredient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RecipeIngredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

}
