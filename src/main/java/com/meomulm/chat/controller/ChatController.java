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
     * 메시지 전송 API (수정됨)
     * POST /api/chat/message
     * Body: "사용자 메시지 텍스트" (plain text)
     */
    @PostMapping("/message")
    public ResponseEntity<ChatMessage> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String message
    ) {
        try {
            int currentUserId = authUtil.getCurrentUserId(authHeader);
            log.info("메시지 전송 요청 - 사용자 ID: {}, 메시지: {}", currentUserId, message);

            ChatMessage response = chatService.sendMessage(currentUserId, message);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 대화방 기록 메시지 기록 조회
     * GET /api/chat/conversations/{conversationId}
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getUserConversationHistory(
            @PathVariable Long conversationId
    ) {
        try {
            log.info("대화 목록 조회 요청 - 대화방 ID: {}", conversationId);

            List<ChatMessage> messages = chatService.getConversationHistory(conversationId);

            if (messages.isEmpty()) {
                log.info("대화 기록이 없습니다 - 대화방 ID : {}", conversationId);
            }
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("대화 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용자의 모든 대화방 기록 메시지 기록 조회
     * GET /api/chat/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversation>> getUserConversations(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            int currentUserId = authUtil.getCurrentUserId(authHeader);

            log.info("사용자 대화 목록 조회 요청 - 사용자 ID: {}", currentUserId);

            List<ChatConversation> conversations = chatService.getUserConversations(currentUserId);
            log.info("사용자 대화 목록 조회 완료 - 대화 수 : {}", conversations.size());

            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("사용자 대화 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}