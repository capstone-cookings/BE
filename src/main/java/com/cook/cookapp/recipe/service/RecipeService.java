package com.cook.cookapp.recipe.service;

import com.cook.cookapp.recipe.dto.res.RecipeResponseDto;
import com.cook.cookapp.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {
    Page<RecipeResponseDto> findAll(Pageable pageable);
    Page<RecipeResponseDto> findByUserId(Long userId, Pageable pageable);
}
