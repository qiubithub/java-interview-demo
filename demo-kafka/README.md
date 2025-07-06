# Kafka开发使用示例

本项目演示了Kafka的基本使用和高级特性，包括生产者、消费者、事务、批处理等功能。

## 功能特点

1. **基本功能**：
   - 消息生产和消费
   - 多主题支持（订单、用户、通知）
   - 异步消息发送和回调

2. **高级特性**：
   - 消息头部（Headers）
   - 事务性消息
   - 批量处理
   - 手动确认模式

3. **模拟模式**：
   - 不依赖Kafka也能运行的模拟功能
   - 模拟生产者和消费者
   - 模拟消息队列和堆积

## 项目结构

- `config/`: Kafka配置类
- `controller/`: API接口
- `model/`: 领域模型
- `service/`: 业务逻辑
  - `KafkaProducerService`: 消息生产者服务
  - `KafkaConsumerService`: 消息消费者服务
  - `KafkaAdvancedFeatureService`: 高级特性演示服务

## API接口

### Kafka真实模式接口（需要Kafka环境）

- `POST /api/kafka/orders`: 发送订单消息
- `POST /api/kafka/users`: 发送用户消息
- `POST /api/kafka/notifications`: 发送通知消息
- `POST /api/kafka/orders/bulk/{count}`: 批量生成订单消息
- `POST /api/kafka/headers`: 发送带头部的消息
- `POST /api/kafka/transaction`: 事务性发送消息
- `POST /api/kafka/batch`: 批量发送消息

### 模拟模式接口（不需要Kafka环境）

- `POST /api/kafka-simulation/orders`: 模拟发送订单消息
- `POST /api/kafka-simulation/users`: 模拟发送用户消息
- `POST /api/kafka-simulation/notifications`: 模拟发送通知消息
- `POST /api/kafka-simulation/orders/bulk/{count}`: 模拟批量生成订单消息
- `POST /api/kafka-simulation/consume/start`: 启动模拟消费
- `POST /api/kafka-simulation/consume/stop`: 停止模拟消费
- `GET /api/kafka-simulation/status`: 获取模拟状态

## 使用方法

### 默认模式：不依赖Kafka（模拟测试）

默认情况下，应用已配置为不依赖Kafka模式，可以直接启动进行测试：

1. 启动本应用
2. 使用`/api/kafka-simulation/`开头的API接口测试Kafka功能

### 使用真实Kafka

如果要使用真实的Kafka进行测试，需要进行以下步骤：

1. 确保已安装并启动Kafka服务器（bootstrap-servers配置为localhost:9092）
2. 修改`application.yml`中的`app.kafka.enabled`为`true`，启用Kafka相关组件
3. 重新启动本应用
4. 使用`/api/kafka/`开头的API接口测试不同的Kafka功能

## Kafka高级特性演示

### 1. 消息头部（Headers）

Kafka消息可以包含自定义头部信息，类似于HTTP头部，用于传递元数据。

```bash
curl -X POST "http://localhost:8081/api/kafka/headers?key=test-key&message=test-message&topic=order-topic"
```

### 2. 事务性消息

Kafka支持事务，确保一组消息要么全部成功发送，要么全部失败。

```bash
curl -X POST "http://localhost:8081/api/kafka/transaction?count=5&topic=order-topic"
```

### 3. 批量处理

批量发送和处理消息，提高吞吐量。

```bash
curl -X POST "http://localhost:8081/api/kafka/batch?count=10&topic=order-topic"
```

## 模拟测试步骤

1. 调用`POST /api/kafka-simulation/orders/bulk/1000`生成1000条消息
2. 调用`POST /api/kafka-simulation/consume/start?consumerCount=3&consumeSpeed=100`启动消费者
3. 通过`GET /api/kafka-simulation/status`查看消息处理情况
4. 调用`POST /api/kafka-simulation/consume/stop`停止消费者

## 参考资料

- [Apache Kafka官方文档](https://kafka.apache.org/documentation/)
- [Spring for Apache Kafka文档](https://docs.spring.io/spring-kafka/reference/html/)