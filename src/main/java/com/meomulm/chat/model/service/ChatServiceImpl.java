package com.meomulm.chat.model.service;

import com.meomulm.accommodation.model.mapper.AccommodationMapper;
import com.meomulm.chat.model.dto.*;
import com.meomulm.chat.model.mapper.ChatKnowMapper;
import com.meomulm.chat.model.mapper.ChatMapper;
import com.meomulm.user.model.mapper.UserMapper;
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
    private final AccommodationMapper accommodationMapper;
    private final UserMapper userMapper;

    private final Random random = new Random();

    @Override
    @Transactional
    public ChatMessage sendMessage(Integer userId, String message) {
        try {
            // 비로그인 사용자 처리
            if (userId == null || userId == 0) {
                log.info("=== 비로그인 사용자 요청 시작 ===");
                log.info("메시지: {}", message);
                ChatMessage response = createGuestResponse(message);
                log.info("=== 비로그인 사용자 응답 완료 ===");
                return response;
            }

            // 로그인 사용자 처리
            log.info("=== 로그인 사용자 (ID: {}) 요청 시작 ===", userId);
            ChatMessage response = createUserResponse(userId, message);
            log.info("=== 로그인 사용자 응답 완료 ===");
            return response;
        } catch (Exception e) {
            log.error("sendMessage 최상위 예외 발생 ", e);
            log.error("에러 타입: {}", e.getClass().getName());
            log.error("에러 메시지: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("원인: {}", e.getCause().getMessage());
            }
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 비로그인 사용자 응답 생성 (DB 저장 X)
     */
    private ChatMessage createGuestResponse(String message) {
        try {
            log.info("비로그인 응답 생성 시작");

            // AI 응답 생성 (DB 데이터 없이)
            String botResponse;
            try {
                botResponse = generateIntelligentResponse(message, 0, 0);
                log.info("AI 응답 생성 성공: {}", botResponse);
            } catch (Exception e) {
                log.error("AI 응답 생성 실패, 기본 응답 사용", e);
                botResponse = "안녕하세요! 머묾 챗봇입니다. 무엇을 도와드릴까요?";
            }

            // 응답 객체 생성
            ChatMessage response = new ChatMessage();
            response.setChatMessageId(0);  // 임시 ID
            response.setConversationId(0);  // 대화방 없음
            response.setMessage(botResponse);
            response.setIsUserMessage(false);
            response.setCreatedAt(LocalDateTime.now());

            log.info("비로그인 응답 객체 생성 완료");
            return response;
        } catch (Exception e) {
            log.error("createGuestResponse 에러 발생", e);

            // 최후의 폴백 - 항상 응답 가능하도록
            ChatMessage fallbackResponse = new ChatMessage();
            fallbackResponse.setChatMessageId(0);
            fallbackResponse.setConversationId(0);
            fallbackResponse.setMessage("죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            fallbackResponse.setIsUserMessage(false);
            fallbackResponse.setCreatedAt(LocalDateTime.now());
            return fallbackResponse;
        }
    }

    /**
     * 로그인 사용자 응답 생성 (DB 저장 O)
     */
    private ChatMessage createUserResponse(int userId, String message) {
        // 기존 대화방 찾기 또는 새로 생성
        ChatConversation conversation = findOrCreateConversation(userId);
        int conversationId = conversation.getChatConversationId();

        log.info("대화방 ID: {}", conversationId);

        // 사용자 메시지 저장
        ChatMessage userMessage = new ChatMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setMessage(message);
        userMessage.setIsUserMessage(true);
        chatKnowMapper.saveMessage(userMessage);
        log.info("사용자 메시지 저장 완료");

        // AI 응답 생성
        String botResponse = generateIntelligentResponse(message, conversationId, userId);
        log.info("AI 응답 생성: {}", botResponse);

        // 봇 응답 저장
        ChatMessage botMessage = new ChatMessage();
        botMessage.setConversationId(conversationId);
        botMessage.setMessage(botResponse);
        botMessage.setIsUserMessage(false);
        chatKnowMapper.saveMessage(botMessage);
        log.info("AI 응답 저장 완료");

        // 응답 반환
        ChatMessage response = new ChatMessage();
        response.setChatMessageId(botMessage.getChatMessageId());
        response.setConversationId(conversationId);
        response.setMessage(botResponse);
        response.setIsUserMessage(false);
        response.setCreatedAt(LocalDateTime.now());

        return response;
    }

    /**
     * 기존 대화방 찾기 또는 새로 생성
     *
     * 개선 사항:
     * - 기존 대화방이 있으면 재사용 (매번 새로 생성하지 않음!)
     * - 없으면 새로 생성
     */
    private ChatConversation findOrCreateConversation(int userId) {
        // 1. 기존 대화방 찾기
        List<ChatConversation> conversations = chatKnowMapper.getUserConversations(userId);

        if (!conversations.isEmpty()) {
            // 가장 최근 대화방 재사용
            ChatConversation latest = conversations.get(0);
            log.info("기존 대화방 재사용 - ID: {}", latest.getChatConversationId());
            return latest;
        }

        // 2. 없으면 새로 생성
        log.info("새 대화방 생성 - 사용자 ID: {}", userId);
        ChatConversation newConversation = new ChatConversation();
        newConversation.setUserId(userId);
        chatKnowMapper.createConversation(newConversation);
        log.info("새 대화방 생성 완료 - ID: {}", newConversation.getChatConversationId());

        return newConversation;
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
        try {
            log.info("응답 생성 시작 - conversationId: {}, userId: {}", conversationId, userId);
            String normalizedMessage = message.toLowerCase().trim();

            // 1단계: 인텐트 기반 응답 시도
            try {
                ChatbotIntent intent = matchIntent(normalizedMessage);
                if (intent != null) {
                    log.info("인텐트 매칭 성공 - {}", intent.getIntentName());

                    // conversationId가 0이 아닐 때만 컨텍스트 저장
                    if (conversationId > 0) {
                        saveContext(conversationId, "last_topic", intent.getIntentName());
                    }
                    return getRandomResponse(intent.getResponse());
                }
            } catch (Exception e) {
                log.warn("인텐트 매칭 실패 (계속 진행): {}", e.getMessage());
            }

            // 2단계: 키워드 기반 지식 베이스 검색
            try {
                ChatbotKnowledge knowledge = matchKnowledge(normalizedMessage);
                if (knowledge != null) {
                    log.info("지식 베이스 매칭 성공 - 카테고리: {}", knowledge.getCategory());

                    // 특수 처리가 필요한 응답
                    if ("time".equals(knowledge.getCategory())) {
                        return getCurrentTimeResponse();
                    }

                    // conversationId가 0이 아닐 때만 컨텍스트 저장
                    if (conversationId > 0) {
                        saveContext(conversationId, "last_topic", knowledge.getCategory());
                    }
                    return knowledge.getAnswer();
                }
            } catch (Exception e) {
                log.warn("지식 베이스 매칭 실패 (계속 진행): {}", e.getMessage());
            }

            // 3단계: 컨텍스트 기반 응답 (로그인 사용자만)
            if (conversationId > 0) {
                try {
                    String contextResponse = getContextBasedResponse(conversationId, normalizedMessage);
                    if (contextResponse != null) {
                        log.info("컨텍스트 기반 응답 사용");
                        return contextResponse;
                    }
                } catch (Exception e) {
                    log.warn("컨텍스트 응답 실패 (계속 진행): {}", e.getMessage());
                }
            }

            // 4단계: AI 응답
            try {
                log.info("Gemini AI 호출 시도 (DB 데이터 수집 중)...");
                StringBuilder dbContext = new StringBuilder();

                // 로그인 사용자만 DB 데이터 수집
                if (userId > 0) {
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
                                log.info("예약 정보 수집 완료: {} 건", reservations.size());
                            }
                        } catch (Exception e) {
                            log.warn("예약 정보 조회 실패: {}", e.getMessage());
                        }
                    }
                }

                // [B] 숙소 추천/검색 정보 조회 (로그인 여부 무관)
                if (message.contains("추천") || message.contains("어때") || message.contains("어디")) {
                    String keyword = message.replaceAll("[^가-힣a-zA-Z0-9]", " ").trim();
                    try {
                        var results = accommodationMapper.selectAccommodationByKeyword(keyword);
                        if (results != null && !results.isEmpty()) {
                            dbContext.append(String.format("\n[%s 검색된 숙소 정보]\n", keyword));
                            results.stream().limit(3).forEach(a -> dbContext.append(String.format("- %s: %s (최저 %d원)\n",
                                    a.getAccommodationName(), a.getAccommodationAddress(), a.getMinPrice())));
                            log.info("숙소 정보 수집 완료: {} 건", results.size());
                        }
                    } catch (Exception e) {
                        log.warn("숙소 검색 실패: {}", e.getMessage());
                    }
                }

                // [최종 호출] Gemini API 호출
                log.info("Gemini API 호출 - 메시지: {}, DB 컨텍스트 길이: {}", message, dbContext.length());
                String aiResponse = geminiService.getGeminiResponse(message, dbContext.toString());

                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    log.info("Gemini 응답 성공: {}", aiResponse.substring(0, Math.min(50, aiResponse.length())));
                    return aiResponse;
                } else {
                    log.warn("Gemini 응답이 비어있음, 기본 응답 사용");
                    return getDefaultResponse();
                }

            } catch (Exception e) {
                log.error("Gemini AI 응답 생성 실패", e);
                log.error("에러 타입: {}", e.getClass().getName());
                log.error("에러 메시지: {}", e.getMessage());
                return getDefaultResponse();
            }

        } catch (Exception e) {
            log.error("generateIntelligentResponse 전체 실패", e);
            return getDefaultResponse();
        }
    }

    /**
     * 인텐트 매칭
     */
    private ChatbotIntent matchIntent(String message) {
        try {
            List<ChatbotIntent> intents = chatMapper.findAllIntents();
            log.info("인텐트 목록 조회 완료: {} 개", intents != null ? intents.size() : 0);

            if (intents == null || intents.isEmpty()) {
                log.warn("등록된 인텐트가 없습니다");
                return null;
            }

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
        } catch (Exception e) {
            log.error("인텐트 매칭 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 지식 베이스 매칭
     */
    private ChatbotKnowledge matchKnowledge(String message) {
        try {
            List<ChatbotKnowledge> knowledgeList = chatMapper.findAllActive();
            log.info("지식 베이스 목록 조회 완료: {} 개", knowledgeList != null ? knowledgeList.size() : 0);

            if (knowledgeList == null || knowledgeList.isEmpty()) {
                log.warn("등록된 지식 베이스가 없습니다");
                return null;
            }

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
        } catch (Exception e) {
            log.error("지식 베이스 매칭 중 오류 발생", e);
            return null;
        }
    }

    /**
     * 컨텍스트 기반 응답
     */
    private String getContextBasedResponse(int conversationId, String message) {
        try {
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
        } catch (Exception e) {
            log.error("컨텍스트 기반 응답 생성 중 오류 발생", e);
            return null;
        }
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
                "안녕하세요! 머묾 챗봇입니다. 숙소 예약에 관해 무엇을 도와드릴까요?",
                "네, 무엇을 도와드릴까요? 숙소 검색이나 예약 관련 질문을 해주세요.",
                "머묾 챗봇입니다. 숙소에 대해 궁금하신 점을 말씀해주세요!",
                "네, 숙소 예약 관련해서 무엇이든 물어보세요!"
        };
        return defaultResponses[random.nextInt(defaultResponses.length)];
    }
}