package com.qiubithub.langchain4j.service;

import com.qiubithub.langchain4j.tool.JavaCodeExampleTool;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaCodeExampleService {

    private final JavaCodeExampleTool javaCodeExampleTool;
    
    @Tool(name = "getJavaCodeExample")
    public String getJavaCodeExample(String concept) {
        log.info("服务请求Java代码示例: {}", concept);
        return javaCodeExampleTool.getJavaCodeExample(concept);
    }
}