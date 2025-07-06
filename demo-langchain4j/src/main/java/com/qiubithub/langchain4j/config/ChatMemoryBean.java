package com.qiubithub.langchain4j.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ChatMemoryBean {

    @Bean
    public ChatMemory chatMemory() {
        // 创建一个聊天记忆实例，设置消息窗口大小为10
        return MessageWindowChatMemory.withMaxMessages(10);
    }
}