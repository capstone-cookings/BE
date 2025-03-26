package com.cook.cookapp.recipe.repository;

import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe save(Recipe recipe);
    Page<Recipe> findByUserId(Long userId, Pageable pageable);

    Optional<Recipe> findByIdAndUserId(Long userId, Long recipeId);
}
