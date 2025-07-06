# Elasticsearch开发使用示例

本项目演示了基于Easy-ES的Elasticsearch开发使用示例，提供了完整的CRUD操作和高级查询功能。

## 功能特点

1. **Easy-ES**：
   - 基于MyBatis-Plus风格的API，对Java开发者友好
   - 链式调用查询，代码简洁易读
   - 简化的CRUD操作，提高开发效率
   - 强大的条件构造器，支持复杂查询场景

2. **高级索引管理**：
   - 索引的创建、删除、更新等操作
   - 索引映射管理
   - 索引设置管理
   - 重建索引功能

3. **高级搜索功能**：
   - 多字段组合搜索
   - 聚合分析
   - 全文搜索
   - 复杂DSL查询支持

4. **模拟模式**：
   - 不依赖Elasticsearch也能运行的模拟功能
   - 当ES不可用时提供基本功能
   - 与真实ES API保持一致的接口，无缝切换

## 项目结构

- `config/`: ES配置类
  - `EasyEsConfig.java`: Easy-ES配置类
  - `RestClientConfig.java`: RestHighLevelClient配置类
- `controller/`: API接口控制器
  - `ProductController.java`: 产品控制器
  - `BlogController.java`: 博客控制器
  - `UserController.java`: 用户控制器
  - `IndexManagementController.java`: 索引管理控制器
  - `AdvancedSearchController.java`: 高级搜索控制器
  - `ElasticsearchSimulationController.java`: 模拟控制器
- `model/`: 领域模型（使用Easy-ES注解）
  - `Product.java`: 产品模型
  - `Blog.java`: 博客模型
  - `User.java`: 用户模型
- `mapper/`: Easy-ES Mapper接口
  - `ProductMapper.java`: 产品Mapper
  - `BlogMapper.java`: 博客Mapper
  - `UserMapper.java`: 用户Mapper
- `service/`: 业务逻辑服务
  - `ProductService.java`: 产品服务
  - `BlogService.java`: 博客服务
  - `UserService.java`: 用户服务
  - `IndexManagementService.java`: 索引管理服务
  - `AdvancedSearchService.java`: 高级搜索服务

## API列表

### 基础CRUD API

- **用户API**：
  - `POST /api/users`: 创建用户
  - `GET /api/users/{id}`: 获取用户
  - `PUT /api/users/{id}`: 更新用户
  - `DELETE /api/users/{id}`: 删除用户
  - `GET /api/users`: 获取所有用户

- **产品API**：
  - `POST /api/products`: 创建产品
  - `GET /api/products/{id}`: 获取产品
  - `PUT /api/products/{id}`: 更新产品
  - `DELETE /api/products/{id}`: 删除产品
  - `GET /api/products`: 获取所有产品

- **博客API**：
  - `POST /api/blogs`: 创建博客
  - `GET /api/blogs/{id}`: 获取博客
  - `PUT /api/blogs/{id}`: 更新博客
  - `DELETE /api/blogs/{id}`: 删除博客
  - `GET /api/blogs`: 获取所有博客

### 高级索引管理API

- `POST /api/indices`: 创建索引
- `DELETE /api/indices/{indexName}`: 删除索引
- `GET /api/indices/{indexName}/exists`: 检查索引是否存在
- `GET /api/indices/{indexName}/mapping`: 获取索引映射
- `PUT /api/indices/{indexName}/mapping`: 更新索引映射
- `GET /api/indices/{indexName}/settings`: 获取索引设置
- `PUT /api/indices/{indexName}/settings`: 更新索引设置
- `GET /api/indices`: 获取所有索引
- `GET /api/indices/{indexName}/stats`: 获取索引统计信息
- `POST /api/indices/reindex`: 重建索引

### 高级搜索API

- `GET /api/search/products`: 多字段组合搜索产品
- `GET /api/search/blogs`: 多字段组合搜索博客
- `GET /api/search/users`: 多字段组合搜索用户
- `GET /api/search/products/aggregations`: 产品聚合分析
- `GET /api/search/blogs/aggregations`: 博客聚合分析
- `GET /api/search/users/aggregations`: 用户聚合分析
- `GET /api/search/fulltext`: 全文搜索（搜索所有类型）
- `POST /api/search/complex`: 复杂搜索（高级DSL查询）

### 模拟API

- `GET /api/simulation/stats`: 获取模拟模式状态
- `GET /api/simulation/health`: 健康检查

## 使用说明

### 前置条件

- Java 17+
- Maven 3.6+
- Elasticsearch 8.x (可选，如果使用模拟模式则不需要)

### 配置

在`application.yml`中配置Elasticsearch连接信息：

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: elastic  # 如果有安全认证
    password: changeme # 如果有安全认证

# Easy-ES配置
easy-es:
  enabled: true
  # 其他配置...

# 应用配置
app:
  es:
    enabled: true  # 是否启用ES，设置为false可在ES不可用时禁用相关功能
    indices:
      product: products
      blog: blogs
      user: users
    # 高级配置
    advanced:
      # 索引设置
      index:
        number_of_shards: 3
        number_of_replicas: 1
        max_result_window: 10000
        refresh_interval: "1s"
      # 搜索设置
      search:
        max_size: 10000
        default_page_size: 10
        highlight:
          pre_tags: "<em>"
          post_tags: "</em>"
      # 分析器设置
      analysis:
        default_analyzer: "ik_max_word"
        default_search_analyzer: "ik_smart"
      # 模拟模式设置
      simulation:
        enabled: false
        delay_ms: 100
```

### 启动应用

```bash
mvn spring-boot:run
```

### 使用模拟模式（ES不可用时）

如果Elasticsearch不可用，可以使用模拟模式：

1. 修改`application.yml`中的`app.es.enabled`为`false`，禁用ES相关组件
2. 重新启动本应用
3. 使用`/api/simulation/`开头的API接口测试基本功能

## 高级功能示例

### 1. 创建自定义索引

```bash
curl -X POST "http://localhost:8083/api/indices?indexName=custom_products&modelType=product"
```

### 2. 多字段组合搜索产品

```bash
curl -X GET "http://localhost:8083/api/search/products?keyword=手机&minPrice=1000&maxPrice=5000&brand=华为"
```

### 3. 产品聚合分析

```bash
curl -X GET "http://localhost:8083/api/search/products/aggregations"
```

### 4. 复杂DSL查询

```bash
curl -X POST "http://localhost:8083/api/search/complex" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must": [
          { "match": { "name": "手机" } }
        ],
        "filter": [
          { "range": { "price": { "gte": 1000, "lte": 5000 } } }
        ]
      }
    },
    "aggs": {
      "by_brand": {
        "terms": { "field": "brand.keyword" }
      }
    }
  }'
```

### 5. 全文搜索（跨多个索引）

```bash
curl -X GET "http://localhost:8083/api/search/fulltext?keyword=华为"
```

### 6. 重建索引

```bash
curl -X POST "http://localhost:8083/api/indices/reindex?sourceIndex=old_products&targetIndex=new_products&modelType=product"
```

### 7. 更新索引设置

```bash
curl -X PUT "http://localhost:8083/api/indices/products/settings" \
  -H "Content-Type: application/json" \
  -d '{
    "number_of_replicas": 2,
    "refresh_interval": "5s"
  }'
```

### 8. 获取索引统计信息

```bash
curl -X GET "http://localhost:8083/api/indices/products/stats"
```

## 注意事项

1. 确保Elasticsearch服务已启动并可访问
2. 如果使用IK分词器，需要在Elasticsearch中安装对应版本的IK分词器插件
3. 如果遇到跨域问题，可以在Elasticsearch配置文件中添加CORS设置
4. 高级搜索功能需要RestHighLevelClient支持，确保依赖正确配置
5. 模拟模式下部分高级功能可能不可用或行为与实际ES有差异

## 高级特性详解

### 1. 索引管理

索引管理功能允许您创建、删除和管理Elasticsearch索引。主要功能包括：

- **创建索引**：使用自定义映射和设置创建索引
- **删除索引**：删除现有索引
- **更新映射**：修改现有索引的映射
- **更新设置**：修改现有索引的设置
- **重建索引**：将数据从一个索引复制到另一个索引

### 2. 高级搜索

高级搜索功能提供了强大的查询能力，包括：

- **多字段组合搜索**：在多个字段上执行复杂查询
- **聚合分析**：执行各种聚合操作，如分组、计数、平均值等
- **全文搜索**：跨多个索引执行全文搜索
- **复杂DSL查询**：支持完整的Elasticsearch DSL查询语法

### 3. 模拟模式

模拟模式允许您在Elasticsearch不可用时仍能使用基本功能：

- **内存存储**：使用内存数据结构模拟Elasticsearch存储
- **基本查询**：支持简单的过滤和排序操作
- **API兼容**：与实际ES API保持一致的接口

## 性能优化建议

1. **索引设计优化**：
   - 合理设置分片数量（通常每个分片不超过30GB）
   - 根据查询需求优化映射
   - 对频繁查询的字段使用keyword类型

2. **查询优化**：
   - 使用过滤器（filter）而非查询（query）进行精确匹配
   - 避免使用通配符前缀查询
   - 合理设置分页大小，避免深度分页

3. **聚合优化**：
   - 在聚合查询中设置size=0，不返回文档
   - 对大数据集使用近似聚合
   - 考虑使用预计算或缓存聚合结果

## 项目更新日志

### 2023-06-15 - 添加高级特性

1. **索引管理增强**：
   - 添加了完整的索引生命周期管理功能
   - 支持自定义映射和设置
   - 实现了重建索引功能，支持数据迁移
   - 添加了索引统计信息API

2. **高级搜索功能**：
   - 实现了多字段组合搜索，支持复杂条件过滤
   - 添加了聚合分析功能，支持各种聚合操作
   - 实现了全文搜索，可跨多个索引搜索
   - 支持复杂DSL查询，满足各种高级搜索需求

3. **模拟模式增强**：
   - 扩展了模拟控制器，支持高级特性的模拟
   - 实现了高级搜索和索引管理的模拟API
   - 提供了更真实的模拟数据和响应

4. **配置优化**：
   - 添加了高级配置选项，支持更细粒度的控制
   - 优化了默认配置，提高性能和可用性
   - 添加了更详细的文档和示例

5. **代码质量**：
   - 重构了服务实现，提高代码质量和可维护性
   - 优化了异常处理和日志记录
   - 完善了注释和文档