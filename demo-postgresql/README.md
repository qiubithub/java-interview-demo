# PostgreSQL 高级应用模块

本模块演示了在Spring Boot应用中使用PostgreSQL数据库的高级特性和最佳实践。

## 功能特点

- 复杂关系模型设计与实现
- 高级SQL查询和性能优化
- JSON数据类型的使用
- 事务管理和并发控制
- 分区表与分表策略
- 全文搜索功能
- 数据库索引优化
- 存储过程和函数的使用

## 技术栈

- Spring Boot
- PostgreSQL (关系型数据库)
- MyBatis (SQL映射框架)
- HikariCP (连接池)

## 数据模型

- 用户(User)：用户基本信息
- 部门(Department)：部门信息
- 项目(Project)：项目信息
- 任务(Task)：任务信息，包含复杂状态流转
- 文档(Document)：文档信息，使用JSON存储元数据
- 评论(Comment)：评论信息，实现树形结构
- 审计日志(AuditLog)：系统操作审计日志

## API接口

- 用户管理API
- 部门管理API
- 项目管理API
- 任务管理API
- 文档管理API
- 统计分析API
- 全文搜索API

## 高级特性演示

- 递归查询(WITH RECURSIVE)
- 窗口函数(WINDOW FUNCTIONS)
- 通用表达式(CTE)
- JSON操作
- 行级锁与事务隔离级别
- 触发器应用
- 物化视图

## 配置说明

详细配置请参考`application.yml`文件。