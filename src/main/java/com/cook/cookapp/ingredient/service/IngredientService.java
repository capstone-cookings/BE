package com.cook.cookapp.ingredient.service;

import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IngredientService {
    void addIngredient(Long userId, IngredientDtoReq ingredientDtoReq, MultipartFile ingredientImage);
    Page<IngredientDtoRes> getAllIngredientsByCreatedAt(Long userId, Pageable pageable);
    Page<IngredientDtoRes> getAllIngredientsByUseByDate(Long userId, Pageable pageable);
    void deleteIngredient(Long userId, Long ingredientId);
    void updateIngredient(Long userId, Long ingredientId, IngredientDtoReq ingredientDtoReq, MultipartFile ingredientImage);
    void updateAlarmStatus(Long userId, Long ingredientId, boolean alarmStatus);
    IngredientDtoRes getIngredientById(Long userId, Long ingredientId);
}
