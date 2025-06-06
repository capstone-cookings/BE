package com.cook.cookapp.recipe.repository;

import com.cook.cookapp.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe save(Recipe recipe);
    Page<Recipe> findByUserId(Long userId, Pageable pageable);
    Recipe findByUserId(Long userId);
    Optional<Recipe> findByIdAndUserId(Long recipeId, Long userId);
    Optional<Recipe> findByUserIdAndTitleAndInstructions(Long userId, String title, String instructionsText);
}
