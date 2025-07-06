package com.qiubithub.langchain4j.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class JavaInterviewAssistantService {

    private final ChatModel chatLanguageModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatMemory chatMemory;
    private final JavaInterviewAssistant assistant;

    public JavaInterviewAssistantService(
            ChatModel chatLanguageModel,
            EmbeddingStore<TextSegment> embeddingStore,
            ChatMemory chatMemory,
            JavaCodeExampleService codeExampleService) {
        
        this.chatLanguageModel = chatLanguageModel;
        this.embeddingStore = embeddingStore;
        this.chatMemory = chatMemory;
        
        // 创建AI助手服务
        this.assistant = AiServices.builder(JavaInterviewAssistant.class)
                .chatModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .tools(codeExampleService)
                .build();
    }

    public String chat(String userMessage) {
        log.info("收到用户消息: {}", userMessage);
        String response = assistant.chat(userMessage);
        log.info("生成的回复: {}", response);
        return response;
    }

    public List<ChatMessage> getChatHistory() {
        return chatMemory.messages();
    }
    
    // 定义AI助手接口
    public interface JavaInterviewAssistant {
        
        @SystemMessage("""
            你是一位精通Java、Spring Boot和软件工程的Java面试助手专家。
            你的目标是通过提供准确、简洁和有帮助的信息，帮助用户准备Java面试。
            
            在回答问题时：
            1. 适当使用从知识库中检索到的相关信息
            2. 在适当的情况下使用getJavaCodeExample工具提供代码示例
            3. 保持解释简洁但全面
            4. 如果你不知道某些内容，坦诚承认而不是编造信息
            5. 使用markdown代码块并添加适当的语言标签来格式化代码片段
            
            请记住，你是在帮助某人准备技术面试，因此你的回答应该技术准确并展示最佳实践。
            """)
        String chat(String message);
    }
}