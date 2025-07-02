# 线程池示例模块

本模块展示了Java线程池的高级用法和最佳实践。

## 主要功能

1. **线程池的基本配置**
   - IO密集型任务线程池
   - CPU密集型任务线程池
   - 混合型任务线程池
   - 自定义线程池

2. **分布式锁**
   - 基于Redis的分布式锁实现
   - 分布式锁注解
   - 锁切面实现

3. **异步任务**
   - 基本异步任务
   - 高级异步任务（链式、组合、并行处理）
   - 错误处理

4. **线程池监控**
   - 线程池指标监控
   - 运行时参数调整
   - Prometheus集成

5. **定时任务**
   - 基于线程池的定时任务
   - 定时任务控制

## API接口

### 线程池操作

- `GET /demo/api/thread-pool/io-task` - 执行IO密集型任务
- `GET /demo/api/thread-pool/cpu-task` - 执行CPU密集型任务
- `GET /demo/api/thread-pool/mixed-task` - 执行混合型任务
- `GET /demo/api/thread-pool/all-tasks` - 执行多种类型任务并等待所有任务完成

### 分布式锁

- `GET /demo/api/thread-pool/business/with-lock/{businessId}` - 使用分布式锁处理业务
- `GET /demo/api/thread-pool/business/without-lock/{businessId}` - 不使用分布式锁处理业务

### 线程池监控

- `GET /demo/api/thread-pool/monitor` - 获取所有线程池的统计信息
- `GET /demo/api/thread-pool/monitor/{poolName}` - 获取指定线程池的统计信息
- `PUT /demo/api/thread-pool/monitor/{poolName}` - 动态调整线程池参数

### 高级异步任务

- `GET /demo/api/advanced-async/parallel` - 并行处理列表数据
- `GET /demo/api/advanced-async/chain` - 链式处理任务
- `GET /demo/api/advanced-async/combine` - 组合多个异步任务
- `GET /demo/api/advanced-async/error-handling` - 异步任务异常处理

### 定时任务

- `POST /demo/api/scheduled-tasks/start` - 启动定时任务
- `POST /demo/api/scheduled-tasks/stop` - 停止定时任务
- `GET /demo/api/scheduled-tasks/status` - 获取定时任务状态

## 监控指标

- Prometheus监控 - `GET /demo/actuator/prometheus`
- 健康检查 - `GET /demo/actuator/health`
- 线程转储 - `GET /demo/actuator/threaddump`
- 环境信息 - `GET /demo/actuator/env`

## 项目结构

```
src/main/java/com/qiubithub/threadpool/
├── annotation        - 自定义注解
├── aspect            - AOP切面
├── config            - 配置类
├── controller        - 控制器
├── factory           - 工厂类
├── handler           - 处理器
├── lock              - 分布式锁
│   └── impl          - 锁实现
├── model             - 模型类
│   └── common        - 通用模型
├── service           - 服务接口
│   └── impl          - 服务实现
└── ThreadPoolApplication.java - 启动类
```