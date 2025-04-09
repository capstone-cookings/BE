package com.cook.cookapp.chatbot.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chatbot.dto.ChatbotResponse;
import com.cook.cookapp.ingredient.entity.Ingredient;
import com.cook.cookapp.ingredient.repository.IngredientRepository;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    // 사용자별로 이전에 추천받은 모든 요리명을 저장 (버튼 기반 추천용)
    private final Map<Long, Set<String>> recommendedDishes = new java.util.concurrent.ConcurrentHashMap<>();

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.api-key}")
    private String apiKey;

    /**
     * "레시피 추천 받기" 버튼 클릭 시 호출
     * 초기 대화용: 사용자 취향과 보유 식재료 및 추가 지시사항만 반영 (이전 추천 요리 제외 없음)
     */
    public ChatbotResponse recommendRecipe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Ingredient> ingredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).getContent();
        String tastePreference = user.getTastePreference() != null ? user.getTastePreference() : "별다른 취향이 없어요.";

        String additionalInstruction =
                "요리 레시피 하나만 추천해, 반드시 아래의 텍스트 형식으로만 답변해. 아래의 텍스트 외에는 어떤 텍스트도 출력하면 안됩니다.\n" +
                        "\"[요리이름]\\n\" +\n" +
                        "\"[재료] - 재료명1 양, - 재료명2 양\\n\" +\n" +
                        "\"[레시피] 1. 단계1 2. 단계2\\n\"\n" +
                        "단, [레시피] 부분은 최대한 간결하게 출력해";

        String prompt = buildPrompt(tastePreference, ingredients, additionalInstruction);

        return callOpenAi(prompt, userId);
    }

    /**
     * "다른 레시피 추천 받기" 버튼 클릭 시 호출
     * 이전에 추천받은 모든 요리를 제외한 새로운 레시피를 요청하며, 사용자 취향, 보유 식재료 및 추가 지시사항이 적용됨.
     */
    public ChatbotResponse recommendAnotherRecipe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Ingredient> ingredients = ingredientRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).getContent();
        String tastePreference = (user.getTastePreference() == null || user.getTastePreference().isBlank()) ? "별다른 취향이 없어요." : user.getTastePreference();

        Set<String> prevDishes = recommendedDishes.get(userId);
        String exclusionInstruction = "";
        if (prevDishes != null && !prevDishes.isEmpty()) {
            String dishList = prevDishes.stream().collect(Collectors.joining(", "));
            exclusionInstruction = "이전에 추천받은 요리: " + dishList + "\n" +
                    "이전 요리들을 제외하고 ";
        }

        String additionalInstruction = exclusionInstruction +
                "다른 요리 레시피 하나만 추천해, 반드시 아래의 텍스트 형식으로만 답변해, 아래의 텍스트 외에는 어떤 텍스트도 출력하면 안됩니다.\n" +
                "\"[요리이름]\\n\" +\n" +
                "\"[재료] - 재료명1 양, - 재료명2 양\\n\" +\n" +
                "\"[레시피] 1. 단계1 2. 단계2\\n\"\n" +
                "단, [레시피] 부분은 최대한 간결하게 출력해";

        String prompt = buildPrompt(tastePreference, ingredients, additionalInstruction);

        return callOpenAi(prompt, userId);
    }

    /**
     * GPT 호출 및 응답 처리
     * 응답의 첫 줄을 추출해 추천받은 요리명을 저장 (모든 추천 요리에 추가)
     */
    private ChatbotResponse callOpenAi(String prompt, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4-1106-preview",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 1000
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String reply = (String) message.get("content");

            // 응답의 첫 줄을 요리명으로 간주하여 저장 (모든 추천 요리에 추가)
            String recommendedDish = reply.split("\\n")[0].replace("[", "").replace("]", "").trim();
            recommendedDishes.computeIfAbsent(userId, k -> new HashSet<>()).add(recommendedDish);

            return new ChatbotResponse(reply);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.OPENAI_API_ERROR);
        }
    }

    /**
     * prompt 생성 메서드
     * 사용자 취향, 보유 식재료 및 추가 지시사항을 통합하여 반환
     */
    private String buildPrompt(String tastePreference, List<Ingredient> ingredients, String additionalInstruction) {
        String ingredientList = ingredients.isEmpty() ? "현재 재료가 없어요." :
                ingredients.stream().map(Ingredient::getFoodName).collect(Collectors.joining(", "));

        return String.format(
                "당신은 한국에 사는 자취생(1~2인 가구)을 위한 요리 레시피 추천 챗봇입니다.\n" +
                        "사용자의 음식 취향: %s\n" +
                        "보유 식재료: %s\n" +
                        "%s",
                tastePreference,
                ingredientList,
                additionalInstruction
        );
    }
}