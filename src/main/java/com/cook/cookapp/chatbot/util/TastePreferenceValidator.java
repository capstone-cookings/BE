package com.cook.cookapp.chatbot.util;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;

import java.util.List;

public class TastePreferenceValidator {

    // 금칙어 리스트 (지시어, 명령, 시스템 프롬프트 변경 유도 단어 등)
    private static final List<String> forbiddenKeywords = List.of(
            "지켜", "명령", "시스템", "프롬프트", "무시", "규칙 변경", "모든 규칙", "지침", "따르지 마", "override"
    );

    private static final int MAX_LENGTH = 50;  // 길이 제한 (필요 시 조정)

    public static void validateTastePreference(String input) {
        if (input.length() > MAX_LENGTH) {
            throw new GeneralException(ErrorStatus.INVALID_INPUT_VALUE);
        }

        String lowerInput = input.toLowerCase();
        for (String keyword : forbiddenKeywords) {
            if (lowerInput.contains(keyword)) {
                throw new GeneralException(ErrorStatus.INVALID_INPUT_VALUE);
            }
        }
    }
}
