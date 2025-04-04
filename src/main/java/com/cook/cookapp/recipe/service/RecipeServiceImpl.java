package com.cook.cookapp.recipe.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.global.util.AmazonS3Util;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.ingredient.repository.IngredientRepository;
import com.cook.cookapp.recipe.converter.RecipeConverter;
import com.cook.cookapp.recipe.dto.req.RecipeDtoReq;
import com.cook.cookapp.recipe.dto.res.RecipeDtoRes;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.entity.RecipeIngredient;
import com.cook.cookapp.recipe.repository.RecipeIngredientRepository;
import com.cook.cookapp.recipe.repository.RecipeRepository;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeConverter recipeConverter;
    private final AmazonS3Util amazonS3Util;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    @Override
    public Page<RecipeDtoRes.MyRecipeRes> findByUserId(Long userId, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findByUserId(userId, pageable);

        return recipes.map(recipe -> {
            Long recipeId = recipe.getId();
            String imageUrl = amazonS3Util.getRecipePath(recipeId);

            RecipeDtoRes.MyRecipeRes userRecipeRes = recipeConverter.toDto(recipe);
            userRecipeRes.setImageUrl(imageUrl);

            return userRecipeRes;
        });
    }

    public void parseChatbotRecipe(Long userId, RecipeDtoReq.StoreRecipeReq request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            String[] sections = request.getRecipe().split("\n");

            if (sections.length < 3) {
                throw new GeneralException(ErrorStatus.INVALID_RECIPE_FORMAT);
            }

            String title = sections[0].replaceAll("[\\[\\]]", "").trim();

            List<String> ingredientNames = new ArrayList<>();
            String ingredientLine = sections[1].replace("[재료]", "").trim();
            String[] ingredientParts = ingredientLine.split("[,\\n-]+");

            for (String ingredient : ingredientParts) {
                String cleanedIngredient = ingredient.trim();
                if (!cleanedIngredient.isEmpty()) {
                    ingredientNames.add(cleanedIngredient);
                }
            }

            StringBuilder instructions = new StringBuilder();
            String recipeText = sections[2].replace("[레시피]", "").trim();

            Pattern pattern = Pattern.compile("(\\d+\\.\\s*)(.*)");
            Matcher matcher = pattern.matcher(recipeText);

            while (matcher.find()) {
                instructions.append(matcher.group(1)) // 🔥 숫자 유지 (ex: "1. ")
                        .append(matcher.group(2).trim()) // 🔥 단계 내용 추가
                        .append("\n");
            }

            String instructionsText = instructions.toString().trim();
            if (instructionsText.isEmpty()) {
                throw new GeneralException(ErrorStatus.INVALID_RECIPE_FORMAT);
            }

            Recipe recipe = Recipe.builder()
                    .title(title)
                    .instructions(instructionsText)
                    .user(user)
                    .build();

            recipeRepository.save(recipe);

            List<RecipeIngredient> recipeIngredients = ingredientNames.stream()
                    .map(name -> {
                        Ingredient ingredient = ingredientRepository.findByFoodName(name)
                                .orElseGet(() -> {
                                    Ingredient newIngredient = new Ingredient();
                                    newIngredient.setFoodName(name);
                                    return ingredientRepository.save(newIngredient);
                                });
                        return new RecipeIngredient(null, recipe, ingredient); // ❌ ID를 직접 설정하지 않음
                    })
                    .collect(Collectors.toList());

            recipe.setRecipeIngredients(recipeIngredients);
            recipeRepository.save(recipe);
            recipeIngredientRepository.saveAll(recipeIngredients);


        } catch (GeneralException e) {
            throw e; // 미리 정의된 예외는 그대로 던짐
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException(ErrorStatus.FAIL_OOOOO); // 명확한 에러 메시지 제공
        }
    }



    public RecipeDtoRes.IsLikedRecipeRes likeRecipe(Long userId, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));

        // 본인이 등록한 레시피인지 확인
        if (!recipe.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED_ACCESS);
        }

        // 좋아요 상태 토글
        recipe.setLiked(!recipe.isLiked());

        return RecipeDtoRes.IsLikedRecipeRes.builder()
                .isLiked(recipe.isLiked())
                .build();
    }

    public RecipeDtoRes.UserRecipeRes getRecipeById(Long recipeId,Long userId){
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId).orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));
        String imageUrl = amazonS3Util.getRecipePath(recipeId);

        List<String> ingredients = recipe.getRecipeIngredients()
                .stream()
                .map(ri -> ri.getIngredient().getFoodName())
                .collect(Collectors.toList());

        return RecipeDtoRes.UserRecipeRes.builder()
                .id(recipeId)
                .title(recipe.getTitle())
                .imageUrl(imageUrl)
                .ingredients(ingredients)
                .instructions(recipe.getInstructions())
                .isLiked(recipe.isLiked())
                .build();
    }

    public void updateRecipe(Long recipeId, Long userId, RecipeDtoReq.RecipeReq requestDto) {
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));
        recipe.update(requestDto);
        recipe.getRecipeIngredients().clear();
        List<RecipeIngredient> currentIngredients = recipe.getRecipeIngredients();
        Set<String> existingIngredientNames = currentIngredients.stream()
                .map(ri -> ri.getIngredient().getFoodName())
                .collect(Collectors.toSet());

        for (String name : requestDto.getIngredients()) {
            if (!existingIngredientNames.contains(name)) { // 기존에 없는 재료만 추가
                Ingredient ingredient = ingredientRepository.findByFoodName(name)
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setFoodName(name);
                            return ingredientRepository.save(newIngredient);
                        });

                RecipeIngredient recipeIngredient = new RecipeIngredient(null, recipe, ingredient);
                currentIngredients.add(recipeIngredient);
            }
        }


        recipeRepository.save(recipe);
        recipeIngredientRepository.saveAll(currentIngredients);

    }


    @Override
    public void deleteRecipe(Long recipeId, Long userId) {
        // 1. 레시피 정보 조회 (삭제할 이미지 URL 가져오기)
        Recipe recipe = recipeRepository.findByIdAndUserId(recipeId, userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));

        String imageUrl = amazonS3Util.getRecipePath(recipeId);
        System.out.println("🔍 삭제할 이미지 URL: " + imageUrl);

        // 2. S3에서 이미지 삭제
        if (imageUrl != null && !imageUrl.isEmpty()) {
            amazonS3Util.deleteRecipeImage(imageUrl);
        }

        // 3. 레시피 삭제
        recipeRepository.delete(recipe);
    }

}
