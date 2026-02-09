package com.meomulm.chat.controller;

import com.meomulm.chat.model.dto.ChatConversation;
import com.meomulm.chat.model.dto.ChatMessage;
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
     * 메세지 전송 API
     * POST /api/chat/message
     */
    @PostMapping("/{message}")
    public ResponseEntity<ChatMessage> sendMessage(@RequestHeader(value = "Authorization") String authHeader, @RequestBody String message) {
        int currentUserId = authUtil.getCurrentUserId(authHeader);
        ChatMessage response = chatService.sendMessage(currentUserId, message);
        return ResponseEntity.ok(response);
    }

    /**
     * 대화방 기록 메세지 기록 조회
     * GET /api/chat/conversations/{conversationId}
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getUserConversationHistory(
            @PathVariable int conversationId
    ){
        try {
            log.info("대화 목록 조회 요청 - 대화방 ID: {}",conversationId);

            List<ChatMessage> messages = chatService.getConversationHistory(conversationId);

            if(messages.isEmpty()){
                log.info("대화 기록이 없습니다 - 대화방 ID : {}",conversationId);
            }
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("대화 목록 조회 중 오류 발생",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 비로그인 시
     */

    /**
     * 사용자의 모든 대화방 기록 메세지 기록 조회
     * GET /api/chat/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversation>> getUserConversations(
            @RequestHeader("Authorization") String authHeader
    ){
        try {
            int currentUserId = authUtil.getCurrentUserId(authHeader);

            log.info("사용자 대화 목록 조회 요청 - 사용자 ID: {}",currentUserId);

            List<ChatConversation> conversations = chatService.getUserConversations(currentUserId);;
            log.info("사용자 대화 목록 조회 완료 - 대화 수 : {}",conversations.size());

            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("사용자 대화 목록 조회 중 오류 발생",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
