package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private int chatMessageId;
    // 어느 대화방의 메세지인가
    private int conversationId;
    // 메세지 내용
    private String message;
    // 사용자 메세지(true) vs 봇 메세지 (false)
    private Boolean isUserMessage;
    // 메세지 전송 시간
    private LocalDateTime createdAt;
}
