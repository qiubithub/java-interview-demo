package com.qiubithub.postgresql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * PostgreSQL高级应用程序入口
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.qiubithub.postgresql.mapper")
public class PostgreSqlApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostgreSqlApplication.class, args);
    }
}