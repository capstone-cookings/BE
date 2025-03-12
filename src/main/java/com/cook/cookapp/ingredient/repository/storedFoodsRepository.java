package com.cook.cookapp.ingredient.repository;

import com.cook.cookapp.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface storedFoodsRepository extends JpaRepository<Ingredient, Long> {
}
