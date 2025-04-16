package com.cook.cookapp.ingredient.repository;

import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.ingredient.entity.IngredientImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientImageRepository extends JpaRepository<IngredientImage, Long> {

    IngredientImage findByIngredient(Ingredient ingredient);
}
