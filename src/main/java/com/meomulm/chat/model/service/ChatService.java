package com.meomulm.chat.model.service;

import com.meomulm.chat.model.dto.ChatConversation;
import com.meomulm.chat.model.dto.ChatMessage;

import java.util.List;

public interface ChatService {
    ChatMessage sendMessage(int userId, String message);

    List<ChatMessage> getConversationHistory(int conversationId);

    List<ChatConversation> getUserConversations(int userId);
}
