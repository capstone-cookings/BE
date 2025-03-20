package com.cook.cookapp.recipe.service;

import com.cook.cookapp.recipe.dto.res.RecipeResponseDto;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    @Override
    public Page<RecipeResponseDto> findAll(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(RecipeResponseDto::fromEntity);
    }
    @Override
    public Page<RecipeResponseDto> findByUserId(Long userId, Pageable pageable) {
        return recipeRepository.findByUserId(userId, pageable)
                .map(RecipeResponseDto::fromEntity); // ✅ 내 레시피만 조회
    }

}
