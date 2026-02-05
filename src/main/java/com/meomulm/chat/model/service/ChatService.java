package com.meomulm.chat.model.service;

import com.meomulm.chat.model.dto.ChatConversation;
import com.meomulm.chat.model.dto.ChatMessage;
import com.meomulm.chat.model.dto.ChatRequest;
import com.meomulm.chat.model.dto.ChatResponse;

import java.util.List;

public interface ChatService {
    ChatResponse sendMessage(ChatRequest request);

    List<ChatMessage> getConversationHistory(Long conversationId);

    List<ChatConversation> getUserConversations(String userId);
}
