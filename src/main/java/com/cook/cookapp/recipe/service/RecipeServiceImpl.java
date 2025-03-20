package com.cook.cookapp.recipe.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.recipe.converter.RecipeConverter;
import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
import com.cook.cookapp.recipe.dto.res.RecipeDtoRes;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.repository.RecipeRepository;
import com.cook.cookapp.user.converter.UserConverter;
import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeConverter recipeConverter;

    @Override
    public Page<RecipeDtoRes.UserRecipeRes> findAll(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeConverter::toDto);
    }
    @Override
    public Page<RecipeDtoRes.UserRecipeRes> findByUserId(Long userId, Pageable pageable) {
        return recipeRepository.findByUserId(userId, pageable)
                .map(recipeConverter::toDto); // ✅ 내 레시피만 조회
    }

    public void addRecipe(Long userId, RecipeDtoReq.RecipeReq requestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Recipe recipe = recipeConverter.toEntity(requestDto, user);
        recipeRepository.save(recipe);
    }

}
