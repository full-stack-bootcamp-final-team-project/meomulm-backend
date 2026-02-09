package com.meomulm.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotIntent {
    // 고유 ID
    private int chatbotIntentId;
    // 인텐트 이름
    private String intentName;
    private String description;
    private String[] patterns;
    private String[] response;
    private Boolean isActive;
    private LocalDateTime createdAt;
}