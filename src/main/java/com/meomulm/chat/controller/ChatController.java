package com.meomulm.chat.controller;

import com.meomulm.chat.model.dto.ChatConversation;
import com.meomulm.chat.model.dto.ChatMessage;
import com.meomulm.chat.model.dto.ChatRequest;
import com.meomulm.chat.model.service.ChatService;
import com.meomulm.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AuthUtil authUtil;
    private final ChatService chatService;

    /**
     * 메시지 전송 API (로그인/비로그인 모두 지원)
     * POST /api/chat
     *
     * 개선사항:
     * 1. PathVariable 제거 - URL에 메시지 넣지 않음
     * 2. Authorization 헤더 optional - 비로그인 가능
     * 3. RequestBody로 JSON 받음
     */
    @PostMapping
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ChatRequest request
    ) {
        try {
            String message = request.getMessage();

            if (message == null || message.trim().isEmpty()) {
                log.warn("빈 메시지 수신");
                return ResponseEntity.badRequest().build();
            }

            // 토큰이 있으면 userId 추출, 없으면 null
            Integer userId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    userId = authUtil.getCurrentUserId(authHeader);
                    log.info("로그인 사용자 메시지 - userId: {}, 메시지: {}", userId, message);
                } catch (Exception e) {
                    log.warn("토큰 파싱 실패, 비로그인으로 처리: {}", e.getMessage());
                }
            } else {
                log.info("비로그인 사용자 메시지 - 메시지: {}", message);
            }

            // userId가 null이면 비로그인 처리
            ChatMessage response = chatService.sendMessage(userId, message);

            log.info("응답 전송 - 응답: {}", response.getMessage());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 대화방 기록 조회 (로그인 필수)
     * GET /api/chat/conversations/{conversationId}
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getUserConversationHistory(
            @PathVariable int conversationId
    ){
        try {
            log.info("대화 목록 조회 요청 - 대화방 ID: {}", conversationId);

            List<ChatMessage> messages = chatService.getConversationHistory(conversationId);

            if(messages.isEmpty()){
                log.info("대화 기록이 없습니다 - 대화방 ID: {}", conversationId);
            }
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("대화 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용자의 모든 대화방 조회 (로그인 필수) 방 조회
     * GET /api/chat/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversation>> getUserConversations(
            @RequestHeader("Authorization") String authHeader
    ){
        try {
            int currentUserId = authUtil.getCurrentUserId(authHeader);

            log.info("사용자 대화 목록 조회 요청 - 사용자 ID: {}", currentUserId);

            List<ChatConversation> conversations = chatService.getUserConversations(currentUserId);
            log.info("사용자 대화 목록 조회 완료 - 대화 수: {}", conversations.size());

            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("사용자 대화 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

//ChatConversation  유저아이디 ->conversationId
//Message conversationId->