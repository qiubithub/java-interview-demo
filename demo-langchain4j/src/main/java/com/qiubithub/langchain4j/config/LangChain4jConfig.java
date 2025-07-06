package com.qiubithub.langchain4j.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.base-url}")
    private String baseUrl;

    @Bean
    public ChatModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .baseUrl(baseUrl)
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}