package com.cook.cookapp.chatbot.service;

import com.cook.cookapp.chatbot.dto.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse recommendRecipe(Long userId);
    ChatbotResponse recommendAnotherRecipe(Long userId);
}
