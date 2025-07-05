package com.qiubithub.langchain4j.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


@Service
@Slf4j
public class StreamingChatService {

    private final StreamingChatModel streamingChatModel;
    private final ChatMemory chatMemory;

    public StreamingChatService(
            @Qualifier("openAiStreamingChatModel") StreamingChatModel streamingChatModel,
            ChatMemory chatMemory) {
        this.streamingChatModel = streamingChatModel;
        this.chatMemory = chatMemory;
    }

    public Flux<String> streamingChat(String userMessage) {
        log.info("Starting streaming chat with message: {}", userMessage);
        
        // 创建一个Sink来发射流式响应
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        
        // 将用户消息添加到聊天记忆
        chatMemory.add(UserMessage.from(userMessage));
        
        // 创建系统提示
        String systemPrompt = "You are an expert Java interview assistant. Provide helpful, accurate, and concise answers to Java interview questions.";
        
        // 创建处理流式响应的处理器
        StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
                sink.tryEmitNext(token);
            }

            @Override
            public void onCompleteResponse(dev.langchain4j.model.chat.response.ChatResponse response) {
                // 将AI回复添加到聊天记忆
                chatMemory.add(response.aiMessage());
                sink.tryEmitComplete();
                log.info("Streaming chat completed");
            }

            @Override
            public void onError(Throwable error) {
                sink.tryEmitError(error);
                log.error("Error in streaming chat", error);
            }
        };
        
        // 使用流式模型生成响应
        streamingChatModel.chat(chatMemory.messages(), handler);
        
        return sink.asFlux();
    }
}