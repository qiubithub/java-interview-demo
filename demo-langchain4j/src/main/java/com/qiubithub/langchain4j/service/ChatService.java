package com.qiubithub.langchain4j.service;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatModel chatLanguageModel;

    public String chat(String userMessage) {
        log.info("处理聊天消息: {}", userMessage);
        
        // 使用OpenAI聊天模型
        String response = chatLanguageModel.chat(UserMessage.from(userMessage)).aiMessage().text();
        
        log.info("生成的回复: {}", response);
        return response;
    }
}