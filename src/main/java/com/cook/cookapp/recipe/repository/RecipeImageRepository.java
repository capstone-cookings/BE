package com.cook.cookapp.recipe.repository;

import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.entity.RecipeImage;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeImageRepository extends JpaRepository<RecipeImage, Long> {
    RecipeImage findByRecipe(Recipe recipe);
}
