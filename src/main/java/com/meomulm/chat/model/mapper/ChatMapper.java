package com.meomulm.chat.model.mapper;

import com.meomulm.chat.model.dto.ChatbotContext;
import com.meomulm.chat.model.dto.ChatbotIntent;
import com.meomulm.chat.model.dto.ChatbotKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    ChatbotKnowledge findByKeyword(String keyword);

    List<ChatbotKnowledge> findByCategory(String category);

    List<ChatbotKnowledge> findAllActive();

    ChatbotIntent findIntentByName(String intentName);

    ChatbotIntent findIntentByPattern(String pattern);

    List<ChatbotIntent> findAllIntents();

    void saveContext(ChatbotContext context);

    ChatbotContext getContext(@Param("conversationId") int conversationId,
                              @Param("contextKey") String contextKey);

    void deleteExpiredContext();
}