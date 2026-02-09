package com.meomulm.chat.model.service;

import com.meomulm.chat.model.dto.ChatConversation;
import com.meomulm.chat.model.dto.ChatMessage;

import java.util.List;

public interface ChatService {
    /**
     * 메시지 전송
     * userId를 Integer로 변경 (null 허용 = 비로그인 사용자)
     *
     * @param userId 사용자 ID (null이면 비로그인)
     * @param message 메시지 내용
     * @return AI 응답 메시지
     */
    ChatMessage sendMessage(Integer userId, String message);

    /**
     * 대화방의 메시지 이력 조회
     */
    List<ChatMessage> getConversationHistory(int conversationId);

    /**
     * 사용자의 모든 대화방 조회
     */
    List<ChatConversation> getUserConversations(int userId);
}