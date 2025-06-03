package com.cook.cookapp.ingredient.service;


import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.ingredient.converter.IngredientConverter;
import com.cook.cookapp.ingredient.dto.req.IngredientDtoReq;
import com.cook.cookapp.ingredient.dto.req.IngredientUpdateDtoReq;
import com.cook.cookapp.ingredient.dto.res.IngredientDtoRes;
import com.cook.cookapp.ingredient.entity.Enum.AlarmStatus;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.ingredient.repository.IngredientImageRepository;
import com.cook.cookapp.ingredient.repository.IngredientRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Transactional
public class IngredientServiceImpl implements IngredientService{

    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final IngredientConverter ingredientConverter;
    private final AmazonS3Util amazonS3Util;
    private final IngredientImageRepository ingredientImageRepository;


    @Override
    public void addIngredient(Long userId, IngredientDtoReq ingredientDtoReq, MultipartFile ingredientImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Ingredient ingredient = ingredientConverter.toEntity(ingredientDtoReq, user);
        ingredientRepository.save(ingredient);
        if (ingredientImage != null && !ingredientImage.isEmpty()) {
            try {
                amazonS3Util.ingredientImageUpload(ingredientImage, ingredient.getId(), userId);
            } catch (IOException e) {
                throw new GeneralException(ErrorStatus.INVALID_IMAGE_URL);
            }
        }
    }

    @Override
    public Page<IngredientDtoRes> getAllIngredientsByCreatedAt(Long userId, Pageable pageable) {
        return ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(ingredientConverter::toDto);
    }

    @Override
    public Page<IngredientDtoRes> getAllIngredientsByUseByDate(Long userId, Pageable pageable) {
        return ingredientRepository.findByUserIdOrderByUseByDateAsc(userId, pageable)
                .map(ingredientConverter::toDto);
    }

    @Override
    public void deleteIngredient(Long userId, Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findByIdAndUserId(ingredientId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INGREDIENT_NOT_FOUND));

        ingredientRepository.delete(ingredient);
    }

    @Override
    public void updateIngredient(Long userId, Long ingredientId, IngredientUpdateDtoReq ingredientDtoReq, MultipartFile ingredientImage) {
        Ingredient ingredient = ingredientRepository.findByIdAndUserId(ingredientId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INGREDIENT_NOT_FOUND));

        ingredient.update(ingredientDtoReq);

        if (ingredientImage != null && !ingredientImage.isEmpty()) {
            try {
                amazonS3Util.ingredientImageUpload(ingredientImage, ingredient.getId(), userId);
            } catch (IOException e) {
                throw new GeneralException(ErrorStatus.INVALID_IMAGE_URL);
            }
            //imageurl 있다면 현재 이미지 유지 없다면 삭제하고 싶은 것
        }else{
            if(ingredientDtoReq.getImageUrl()==null || ingredientDtoReq.getImageUrl().isEmpty()) {
                ingredient.setIngredientImage(null);
                ingredientRepository.flush();
                ingredientImageRepository.deleteById(ingredientId);
                ingredientImageRepository.flush();
            }
        }
    }

    @Override
    public void updateAlarmStatus(Long userId, Long ingredientId, boolean alarmStatus) {
        Ingredient ingredient = ingredientRepository.findByIdAndUserId(ingredientId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INGREDIENT_NOT_FOUND));

        ingredient.setAlarmStatus(alarmStatus ? AlarmStatus.ON : AlarmStatus.OFF);
        ingredientRepository.save(ingredient);
    }

    @Override
    public IngredientDtoRes getIngredientById(Long userId, Long ingredientId) {
        Ingredient ingredient = ingredientRepository.findByIdAndUserId(ingredientId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.INGREDIENT_NOT_FOUND));

        return ingredientConverter.toDto(ingredient);
    }
}
