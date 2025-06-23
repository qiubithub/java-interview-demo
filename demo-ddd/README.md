# 领域驱动设计 (DDD) 示例模块

本模块展示了基于领域驱动设计 (Domain-Driven Design) 架构的订单管理系统示例实现。

## 项目架构

项目采用经典的 DDD 分层架构:

```
com.qiubithub.ddd
├── application       # 应用层，处理用例和业务流程编排
│   ├── dto           # 数据传输对象
│   ├── mapper        # DTO与领域对象映射
│   └── service       # 应用服务，协调领域对象完成业务流程
├── domain            # 领域层，核心业务逻辑
│   ├── model         # 领域模型
│   │   ├── aggregate # 聚合根
│   │   ├── entity    # 实体
│   │   └── valueobject # 值对象
│   ├── repository    # 仓储接口，定义持久化需求
│   └── service       # 领域服务，处理跨实体的业务逻辑
├── infrastructure    # 基础设施层，提供技术支持
│   ├── common        # 通用组件
│   ├── exception     # 异常处理
│   ├── persistence   # 持久化实现
│   └── repository    # 仓储实现
└── interfaces        # 接口层，处理外部请求
    └── rest          # REST API接口
        └── controller # 控制器
```

## 领域模型

* **聚合根 (Aggregate Root)**
  * `Order` - 订单聚合根，管理订单生命周期和状态变化
  
* **实体 (Entity)**
  * `OrderItem` - 订单项实体，代表订单中的商品行

* **值对象 (Value Object)**
  * `Money` - 金额值对象，表示货币金额
  * `Address` - 地址值对象，表示收货地址

## 核心业务流程

* **订单创建流程**
  * 创建订单基本信息
  * 添加订单项
  * 计算订单总金额

* **订单状态变更流程**
  * 支付订单
  * 订单发货
  * 确认送达
  * 完成订单
  * 取消订单

* **订单修改流程**
  * 添加/修改/删除订单项
  * 更新收货地址
  * 更新收件人信息

## DDD 模式实践

* **限界上下文** - 订单管理作为一个独立的上下文
* **实体和值对象区分** - 使用值对象表示不变性概念
* **聚合边界** - 订单作为聚合根，订单项作为实体
* **仓储模式** - 使用仓储接口抽象持久化需求
* **领域服务** - 处理跨实体的业务逻辑
* **应用服务** - 协调领域对象完成业务用例
* **贫血/充血模型** - 采用充血模型，业务逻辑封装在领域对象中

## 技术实现

* Spring Boot 3.2.0
* MyBatis 持久层框架
* H2 内存数据库
* PageHelper 分页插件
* MapStruct 对象映射
* JUnit 5 测试

## 运行方式

访问模拟接口，体验完整订单流程:

```
GET http://localhost:8085/ddd/api/simulation/orders/simulate-order-flow
GET http://localhost:8085/ddd/api/simulation/orders/simulate-order-cancel
GET http://localhost:8085/ddd/api/simulation/orders/simulate-order-update
```