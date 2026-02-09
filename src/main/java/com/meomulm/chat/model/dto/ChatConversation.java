package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {
    // 대화방 고유 ID
    private int chatConversationId;
    // 사용자 ID
    private int userId;
    // 대화방 생성 시간
    private LocalDateTime createdAt;
    // 대화방 마지막 업데이트 시간
    private LocalDateTime updatedAt;
}
