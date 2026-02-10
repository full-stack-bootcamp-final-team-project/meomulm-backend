package com.meomulm.chat.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotKnowledge {
    // 지식 고유 ID
    private int chatbotKnowledgeId;
    // 카테고리 (인사, 날씨, 시간 등)
    private String category;
    // 키워드 배열 ["안녕", "하이", "헬로"]
    private String[] keywords;
    // 질문  "안녕하세요" 와 같은 응답 잠시 보유
    private String question;
    // 답변  "안녕하세요" 와 같은 응답 잠시 보유
    private String answer;
    // 우선순위
    private Integer priority;
    // 활성화 여부
    private Boolean isActive;
    // 생성 시간
    private LocalDateTime createdAt;
    // 수정시간
    private LocalDateTime updatedAt;
}