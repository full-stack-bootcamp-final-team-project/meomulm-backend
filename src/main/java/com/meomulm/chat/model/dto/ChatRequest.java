package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    // 요청한 사용자 ID
    private int userId;
    // 사용자가 보낸 메세지
    private String message;
    // 기존 대화방 ID
    private int conversationId;
}
