package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    // 대화방 ID(새로 생성된 경우 포함)
    private Long conversationId;
    // 챗봇의 응답 메세지
    private String message;
    // 응답 시간
    private LocalDateTime timestamp;
}
