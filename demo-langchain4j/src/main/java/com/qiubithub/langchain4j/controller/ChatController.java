package com.qiubithub.langchain4j.controller;

import com.qiubithub.langchain4j.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        log.info("收到聊天请求: {}", userMessage);
        
        String response = chatService.chat(userMessage);
        
        return Map.of("response", response);
    }
}