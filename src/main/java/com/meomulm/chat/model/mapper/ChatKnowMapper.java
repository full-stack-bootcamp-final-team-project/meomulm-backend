package com.meomulm.chat.model.mapper;

import com.meomulm.chat.model.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatKnowMapper {
    void createConversation(ChatConversation conversation);

    void saveMessage(ChatMessage message);

    ChatConversation getConversation(Long id);

    List<ChatMessage> getMessages(int conversationId);

    List<ChatConversation> getUserConversations(int userId);

    void updateConversation(int id);
}
