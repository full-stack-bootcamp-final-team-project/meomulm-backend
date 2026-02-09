package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotContext {
    private int chatbotContextId;
    // 어느 대화방의 맥락인가
    private int conversationId;
    // 맥락 키 "last_topic" "user_name"
    private String contextKey;
    // 맥락 값 "greeting" "홍길동"
    private String contextValue;
    // 만료 시간 (시간 지나면 자동 삭제)
    private LocalDateTime expiresAt;
    // 생성 시간
    private LocalDateTime createdAt;
}