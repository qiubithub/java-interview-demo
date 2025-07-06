package com.qiubithub.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB高级应用程序入口
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.qiubithub.mongodb.repository")
public class MongoDbApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoDbApplication.class, args);
    }
}