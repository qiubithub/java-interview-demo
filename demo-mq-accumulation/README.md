# MQ消息堆积处理示例

本项目演示了不同的MQ消息堆积处理策略，基于RocketMQ实现。

## 消息堆积处理策略

本项目实现了以下几种消息堆积处理策略：

1. **动态扩容消费者线程**：当检测到消息堆积时，自动增加消费者线程数，提高消费能力。
2. **临时队列处理**：将消息转移到内存中的临时队列，使用专门的线程池快速处理。
3. **优先级消费**：对重要消息设置优先级标签，确保重要消息优先被消费。
4. **跳过非关键消息**：在堆积严重时，可以选择跳过一些非关键消息，优先处理重要消息。
5. **批量处理**：将多条消息一次性批量处理，减少处理开销。

## 项目结构

- `config/`: 配置类
- `controller/`: API接口
- `domain/`: 领域模型
- `service/`: 业务逻辑
  - `OrderMessageProducer`: 消息生产者
  - `NormalOrderConsumer`: 普通消息消费者
  - `PriorityOrderConsumer`: 优先级消息消费者
  - `ScalableOrderConsumer`: 可扩展消费者（动态调整线程数）
  - `TemporaryQueueService`: 临时队列服务
  - `MessageAccumulationHandler`: 消息堆积处理器

## API接口

### RocketMQ相关接口

- `POST /api/messages/send`: 发送单个普通订单消息
- `POST /api/messages/send/priority`: 发送优先级订单消息
- `POST /api/messages/generate/{count}`: 批量生成指定数量的消息，模拟堆积
- `POST /api/messages/strategy/{strategy}`: 切换堆积处理策略
- `GET /api/messages/status`: 获取当前状态信息

### 不依赖RocketMQ的模拟接口

- `POST /api/simulation/produce/{count}`: 生成指定数量的模拟消息
- `POST /api/simulation/consume/start?consumerCount=2&consumeSpeed=200`: 启动消费者（可指定消费者数量和消费速度）
- `POST /api/simulation/consume/stop`: 停止所有消费者
- `GET /api/simulation/status`: 获取当前状态
- `POST /api/simulation/queue/clear`: 清空消息队列
- `POST /api/simulation/consume/speed/{speed}`: 调整消费速度（毫秒/消息）
- `POST /api/simulation/consume/scale/{count}`: 调整消费者数量（动态扩缩容）

## 使用方法

### 默认模式：不依赖RocketMQ（模拟测试）

默认情况下，应用已配置为不依赖RocketMQ模式，可以直接启动进行测试：

1. 启动本应用
2. 使用`/api/simulation/`开头的API接口测试消息堆积处理

### 使用RocketMQ（真实MQ）

如果要使用真实的RocketMQ进行测试，需要进行以下步骤：

1. 确保已安装并启动RocketMQ服务器（nameserver运行在localhost:9876）
2. 修改`application.yml`中的`app.mq.enabled`为`true`，启用RocketMQ相关组件
3. 重新启动本应用
4. 使用`/api/messages/`开头的API接口测试不同的消息堆积处理策略

### 依赖问题解决

如果遇到RocketMQ依赖问题：

1. 检查pom.xml中的RocketMQ版本是否与Spring Boot版本兼容
2. 当前配置使用的是RocketMQ 2.2.3版本，与Spring Boot 3.2.0兼容
3. 如果仍有问题，可以尝试其他版本，如2.2.2或2.2.1
4. 确保Maven能够访问到相关仓库（已配置了中央仓库、阿里云镜像和Spring里程碑仓库）

#### 模拟消息堆积测试步骤

1. 调用`POST /api/simulation/produce/10000`生成10000条消息
2. 调用`POST /api/simulation/consume/start?consumerCount=2&consumeSpeed=200`启动2个消费者，每条消息处理时间为200ms
3. 通过`GET /api/simulation/status`查看堆积情况
4. 当发现堆积严重时，可以调用`POST /api/simulation/consume/scale/10`增加消费者数量到10个
5. 或者调用`POST /api/simulation/consume/speed/50`加快消费速度到50ms/消息

## 消息堆积处理策略详解

### 1. 动态扩容消费者线程

当检测到消息堆积时，系统会自动增加消费者线程数，提高消息处理能力。当堆积缓解后，会自动减少线程数，释放资源。

实现类: `ScalableOrderConsumer`

### 2. 临时队列处理

将消息转移到内存中的临时队列，使用专门的高性能线程池快速处理。这种方式适合短时间内的突发流量。

实现类: `TemporaryQueueService`

### 3. 优先级消费

通过消息标签区分优先级，确保重要消息优先被消费。

实现类: `PriorityOrderConsumer`

### 4. 跳过非关键消息

在堆积严重时，可以选择跳过一些非关键消息，只处理重要消息，等堆积缓解后再处理跳过的消息。

实现类: `MessageAccumulationHandler`中的`processWithSkipping`方法

### 5. 批量处理

将多条消息一次性批量处理，减少单条消息的处理开销。

实现类: `MessageAccumulationHandler`中的`processInBatch`方法

## 参考资料

- [视频教程：MQ消息堆积处理](https://www.bilibili.com/video/BV1yT411H7YK/)
- [RocketMQ官方文档](https://rocketmq.apache.org/docs/quick-start/)