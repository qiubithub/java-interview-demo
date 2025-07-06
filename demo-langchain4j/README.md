# LangChain4j 示例项目

本模块演示了如何将 LangChain4j 与 Spring Boot 集成，创建 AI 驱动的应用程序。

## 功能特点

- 使用 OpenAI 模型的简单聊天界面
- 流式聊天响应
- Java 代码示例生成
- 具有上下文感知能力的 Java 面试助手
- 本地嵌入模型（AllMiniLmL6V2）

## 环境要求

- Java 17+
- Maven 3.6+
- OpenAI API 密钥

## 配置

对于生产环境，请将 OpenAI API 密钥设置为环境变量：

```bash
export OPENAI_API_KEY=your-api-key-here
```

对于开发和测试，应用程序在 application.yml 文件中使用占位符 API 密钥：
如果像第三方AI API中转 要加上 中转路径 base_url 比如极客智坊 base-url: https://geekai.co/api/v1

```yaml
openai:
  api:
    key: sk-demo-key-for-testing
```

**注意：** 在进行实际 API 调用之前，您需要将其替换为有效的 OpenAI API 密钥。

## 运行应用程序

```bash
mvn spring-boot:run
```

应用程序默认将在 8089 端口启动。

## API 接口

### 基本聊天

```
POST /api/chat
Content-Type: application/json

{
  "message": "什么是 Spring Boot？"
}
```

## 项目结构

```
src/main/java/com/qiubithub/langchain4j/
├── config/                 # 配置类
│   ├── LangChain4jConfig.java
│   ├── OpenAIConfig.java
│   ├── RetrieverConfig.java
│   └── ChatMemoryBean.java
├── controller/             # REST 控制器
│   └── ChatController.java
├── service/                # 服务层
│   ├── ChatService.java
│   ├── JavaCodeExampleService.java
│   ├── JavaInterviewAssistantService.java
│   └── StreamingChatService.java
├── tool/                   # LangChain4j 自定义工具
│   └── JavaCodeExampleTool.java
└── LangChain4jApplication.java  # 主应用程序类

src/main/resources/
├── application.yml         # 应用程序配置
└── documents/              # 知识库文档
    ├── java_interview_tips.txt
    └── spring_boot_overview.txt
```

## 依赖项

- Spring Boot 3.2.3
- LangChain4j 1.1.0
- Lombok
- Spring WebFlux (用于流式响应)

## 许可证

本项目采用 MIT 许可证。