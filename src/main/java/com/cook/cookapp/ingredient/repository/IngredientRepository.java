package com.cook.cookapp.ingredient.repository;

import com.cook.cookapp.ingredient.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Page<Ingredient> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Ingredient> findByUserIdOrderByUseByDateAsc(Long userId, Pageable pageable);
    Optional<Ingredient> findByIdAndUserId(Long ingredientId, Long userId);
    Optional<Ingredient> findByFoodName(String name);
}
