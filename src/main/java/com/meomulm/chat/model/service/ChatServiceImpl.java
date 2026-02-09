package com.meomulm.chat.model.service;
import com.meomulm.chat.model.dto.*;
import com.meomulm.chat.model.mapper.ChatKnowMapper;
import com.meomulm.chat.model.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatKnowMapper chatKnowMapper;
    private final ChatMapper chatMapper;
    private final GeminiService geminiService;

    // 추가되는 매퍼들(AI에게 실시간 정보를 전달)
    private final com.meomulm.accommodation.model.mapper.AccommodationMapper accommodationMapper;
    private final com.meomulm.user.model.mapper.UserMapper userMapper;

    private final Random random = new Random();

    @Override
    @Transactional
    public ChatMessage sendMessage(int userId, String message) {
        // 새 대화인 경우 생성
        ChatConversation conversation = new ChatConversation();
        conversation.setUserId(userId);
        chatKnowMapper.createConversation(conversation);
        int conversationId = conversation.getChatConversationId();
        log.info("새 대화방 생성 - ID: {}", conversationId);

        // 사용자 메시지 저장
        ChatMessage userMessage = new ChatMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setMessage(message);
        userMessage.setIsUserMessage(true);
        chatKnowMapper.saveMessage(userMessage);

        // AI 응답 생성 (지식 베이스 활용)
        String botResponse = generateIntelligentResponse(message, conversationId, userId);
        log.info("봇 응답 생성: {}", botResponse);

        // 봇 응답 저장
        ChatMessage botMessage = new ChatMessage();
        botMessage.setConversationId(conversationId);
        botMessage.setMessage(botResponse);
        botMessage.setIsUserMessage(false);
        chatKnowMapper.saveMessage(botMessage);

        // 응답 생성
        ChatMessage response = new ChatMessage();
        response.setChatMessageId(botMessage.getChatMessageId());
        response.setConversationId(conversationId);
        response.setMessage(botResponse);
        response.setCreatedAt(LocalDateTime.now());

        return response;
    }

    @Override
    public List<ChatMessage> getConversationHistory(int conversationId) {
        log.info("대화 기록 조회 - 대화방 ID: {}", conversationId);
        return chatKnowMapper.getMessages(conversationId);
    }

    @Override
    public List<ChatConversation> getUserConversations(int userId) {
        log.info("사용자 대화 목록 조회 - 사용자 ID: {}", userId);
        return chatKnowMapper.getUserConversations(userId);
    }

    /**
     * 지식 베이스와 인텐트를 활용한 지능형 응답 생성
     */
    private String generateIntelligentResponse(String message, int conversationId, int userId) {
        String normalizedMessage = message.toLowerCase().trim();

        // 1단계: 인텐트 기반 응답 시도
        ChatbotIntent intent = matchIntent(normalizedMessage);
        if (intent != null) {
            log.info("인텐트 매칭 성공 - {}", intent.getIntentName());
            // 컨텍스트 저장
            saveContext(conversationId, "last_topic", intent.getIntentName());
            return getRandomResponse(intent.getResponse());
        }

        // 2단계: 키워드 기반 지식 베이스 검색
        ChatbotKnowledge knowledge = matchKnowledge(normalizedMessage);
        if (knowledge != null) {
            log.info("지식 베이스 매칭 성공 - 카테고리: {}", knowledge.getCategory());

            // 특수 처리가 필요한 응답
            if ("time".equals(knowledge.getCategory())) {
                return getCurrentTimeResponse();
            }

            // 컨텍스트 저장
            saveContext(conversationId, "last_topic", knowledge.getCategory());
            return knowledge.getAnswer();
        }

        // 3단계: 컨텍스트 기반 응답
        String contextResponse = getContextBasedResponse(conversationId, normalizedMessage);
        if (contextResponse != null) {
            log.info("컨텍스트 기반 응답 사용");
            return contextResponse;
        }

        // 4단계: ai, 기본 응답
        try {
            log.info("Gemini AI 호출 시도 (DB 데이터 수집 중)...");
            StringBuilder dbContext = new StringBuilder();

            // [A] 예약 정보 조회 시도
            if (message.contains("예약") || message.contains("내역")) {
                try {
                    var reservations = userMapper.selectUserReservationById(userId);

                    if (reservations != null && !reservations.isEmpty()) {
                        dbContext.append("\n[고객 예약 정보]\n");
                        for (var res : reservations) {
                            dbContext.append(String.format("- 숙소: %s, 상태: %s\n",
                                    res.getAccommodationName(),
                                    res.getStatus()));
                        }
                    }
                } catch (NumberFormatException e) {
                    log.error("userId 형변환 실패: {}", userId);
                    // userId가 숫자가 아닌 경우(예: 이메일 형태) 처리 로직
                }
            }

            // [B] 숙소 추천/검색 정보 조회 시도
            if (message.contains("추천") || message.contains("어때") || message.contains("어디")) {
                String keyword = message.replaceAll("[^가-힣a-zA-Z0-9]", " ").trim();
                var results = accommodationMapper.selectAccommodationByKeyword(keyword);
                if (results != null && !results.isEmpty()) {
                    dbContext.append(String.format("\n[%s 검색된 숙소 정보]\n", keyword));
                    results.stream().limit(3).forEach(a -> dbContext.append(String.format("- %s: %s (최저 %d원)\n",
                            a.getAccommodationName(), a.getAccommodationAddress(), a.getMinPrice())));
                }
            }

            // [최종 호출] 수집된 DB 데이터와 질문을 함께 보냄
            String aiResponse = geminiService.getGeminiResponse(message, dbContext.toString());
            return (aiResponse == null || aiResponse.trim().isEmpty()) ? getDefaultResponse() : aiResponse;

        } catch (Exception e) {
            log.error("AI 응답 생성 실패", e);
            return getDefaultResponse();
        }
    }

    /**
     * 인텐트 매칭
     */
    private ChatbotIntent matchIntent(String message) {
        List<ChatbotIntent> intents = chatMapper.findAllIntents();

        for (ChatbotIntent intent : intents) {
            if (intent.getPatterns() != null) {
                for (String pattern : intent.getPatterns()) {
                    if (message.contains(pattern.toLowerCase())) {
                        return intent;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 지식 베이스 매칭
     */
    private ChatbotKnowledge matchKnowledge(String message) {
        List<ChatbotKnowledge> knowledgeList = chatMapper.findAllActive();

        ChatbotKnowledge bestMatch = null;
        int highestPriority = -1;

        for (ChatbotKnowledge knowledge : knowledgeList) {
            if (knowledge.getKeywords() != null) {
                for (String keyword : knowledge.getKeywords()) {
                    if (message.contains(keyword.toLowerCase())) {
                        if (knowledge.getPriority() > highestPriority) {
                            bestMatch = knowledge;
                            highestPriority = knowledge.getPriority();
                        }
                        break;
                    }
                }
            }
        }

        return bestMatch;
    }

    /**
     * 컨텍스트 기반 응답
     */
    private String getContextBasedResponse(int conversationId, String message) {
        ChatbotContext context = chatMapper.getContext(conversationId, "last_topic");

        if (context != null) {
            String lastTopic = context.getContextValue();
            log.info("이전 대화 주제: {}", lastTopic);

            // 인사 후 질문에 대한 자연스러운 응답
            if ("greeting".equals(lastTopic)) {
                if (message.contains("날씨") || message.contains("시간")) {
                    ChatbotKnowledge knowledge = matchKnowledge(message);
                    if (knowledge != null) {
                        return "네, " + knowledge.getAnswer();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 컨텍스트 저장
     */
    private void saveContext(int conversationId, String key, String value) {
        try {
            ChatbotContext context = new ChatbotContext();
            context.setConversationId(conversationId);
            context.setContextKey(key);
            context.setContextValue(value);
            context.setExpiresAt(LocalDateTime.now().plusHours(24));
            chatMapper.saveContext(context);
            log.info("컨텍스트 저장 - 키: {}, 값: {}", key, value);
        } catch (Exception e) {
            log.error("컨텍스트 저장 실패", e);
        }
    }

    /**
     * 현재 시간 응답
     */
    private String getCurrentTimeResponse() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("현재 시간은 %d시 %d분입니다.", now.getHour(), now.getMinute());
    }

    /**
     * 배열에서 랜덤 응답 선택
     */
    private String getRandomResponse(String[] responses) {
        if (responses == null || responses.length == 0) {
            return getDefaultResponse();
        }
        return responses[random.nextInt(responses.length)];
    }

    /**
     * 기본 응답
     */
    private String getDefaultResponse() {
        String[] defaultResponses = {
                "죄송합니다. 질문을 이해하지 못했습니다. 다시 말씀해 주시겠어요?",
                "잘 이해하지 못했습니다. 다른 방식으로 질문해 주실 수 있나요?",
                "죄송하지만 그 부분은 잘 모르겠습니다. 다른 질문을 해주세요.",
                "무엇을 도와드릴까요? 좀 더 구체적으로 말씀해 주시면 감사하겠습니다."
        };
        return defaultResponses[random.nextInt(defaultResponses.length)];
    }
}