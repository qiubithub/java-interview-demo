# MongoDB 高级应用模块

本模块演示了在Spring Boot应用中使用MongoDB数据库的高级特性和最佳实践。

## 功能特点

- 文档模型设计与实现
- 复杂查询和聚合操作
- 地理空间索引和查询
- 全文搜索功能
- 时序数据处理
- GridFS文件存储
- 数据分片和复制集
- 事务处理
- 变更流(Change Streams)

## 技术栈

- Spring Boot
- MongoDB (文档型数据库)
- Spring Data MongoDB
- MongoDB Atlas (可选云服务)

## 数据模型

- 用户(User)：用户信息文档
- 内容(Content)：博客、文章等内容
- 评论(Comment)：嵌套评论结构
- 产品(Product)：产品目录，包含复杂属性
- 位置(Location)：地理位置信息
- 日志(Log)：系统操作日志
- 统计(Statistic)：时序统计数据
- 文件(File)：使用GridFS存储的大型文件

## API接口

- 用户管理API
- 内容管理API
- 评论管理API
- 产品管理API
- 地理位置查询API
- 全文搜索API
- 统计分析API
- 文件存储API

## 高级特性演示

- 复杂聚合管道
- 地理空间查询
- 文本索引和搜索
- 时序数据分析
- 大文件存储与检索
- 数据变更监听
- 数据库事务
- 索引策略优化

## 配置说明

详细配置请参考`application.yml`文件。