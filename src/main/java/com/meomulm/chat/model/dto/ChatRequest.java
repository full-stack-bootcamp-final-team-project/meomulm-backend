package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    // 사용자가 보낸 메세지 (필수)
    private String message;
    // 요청한 사용자 ID (선택, 프론트에서 보내지 않음 - 토큰으로 처리)
    private Integer userId;
    // 기존 대화방 ID (선택, 프론트에서 보내지 않음)
    private Integer conversationId;
}