package com.qiubithub.langchain4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LangChain4jApplication {
    public static void main(String[] args) {
        SpringApplication.run(LangChain4jApplication.class, args);
    }
}