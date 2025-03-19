package com.cook.cookapp.recipe.repository;

import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<User, Long> {
    Recipe save(Recipe recipe);
}
