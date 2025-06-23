# Java JDK17 新特性使用案例

本项目演示了Java 9到Java 17引入的主要新特性，包括Record类型、密封类、模式匹配、文本块、Switch表达式等。

## 功能特点

1. **Record类型**：
   - 简洁的不可变数据类
   - 自动生成equals、hashCode和toString方法
   - 紧凑构造函数
   - 嵌套Record和泛型Record

2. **密封类(Sealed Classes)**：
   - 限制继承层次结构
   - 使用permits关键字指定允许的子类
   - 子类使用final、non-sealed或sealed修饰符

3. **模式匹配**：
   - instanceof模式匹配
   - 类型检查和转换的简化
   - 条件表达式中的模式匹配

4. **文本块(Text Blocks)**：
   - 多行字符串字面量
   - 保留格式
   - 转义序列和行连接

5. **Switch表达式**：
   - 作为表达式使用switch
   - 箭头语法和yield关键字
   - 多个case标签
   - 模式匹配switch(预览特性)

6. **Stream API增强**：
   - Stream.ofNullable
   - Stream.iterate重载方法
   - takeWhile和dropWhile方法
   - 不可变集合收集器
   - teeing收集器
   - Stream.toList便捷方法

7. **局部变量类型推断(var)**：
   - 简化变量声明
   - 在循环中使用
   - 在try-with-resources中使用
   - 在lambda表达式中使用(Java 11)

8. **空指针异常改进**：
   - 增强的NullPointerException消息
   - 精确指出哪个部分是null
   - 提高调试效率

## 项目结构

- `features/`: 各种Java新特性的演示类
  - `RecordDemo.java`: Record类型演示
  - `SealedClassDemo.java`: 密封类演示
  - `PatternMatchingDemo.java`: 模式匹配演示
  - `TextBlocksDemo.java`: 文本块演示
  - `SwitchExpressionsDemo.java`: Switch表达式演示
  - `StreamApiEnhancementsDemo.java`: Stream API增强演示
  - `VarTypeInferenceDemo.java`: 局部变量类型推断演示
  - `NullPointerExceptionImprovementDemo.java`: 空指针异常改进演示
- `controller/`: API接口
  - `Java17DemoController.java`: 演示控制器

## API接口

- `GET /api/java17`: 列出所有可用的演示
- `GET /api/java17/{demo}`: 运行指定的演示，其中`{demo}`可以是：
  - `record`: Record类型演示
  - `sealed`: 密封类演示
  - `pattern-matching`: 模式匹配演示
  - `text-blocks`: 文本块演示
  - `switch-expressions`: Switch表达式演示
  - `stream-api`: Stream API增强演示
  - `var-type`: 局部变量类型推断演示
  - `npe-improvement`: 空指针异常改进演示
  - `all`: 运行所有演示

## 使用方法

1. 启动应用
2. 访问 `http://localhost:8083/api/java17` 查看可用的演示
3. 访问 `http://localhost:8083/api/java17/{demo}` 运行指定的演示
4. 查看控制台日志输出，了解演示结果

## 注意事项

- 部分特性（如密封类中的模式匹配switch）是预览特性，需要使用`--enable-preview`编译器标志
- 本项目已在pom.xml中配置了`--enable-preview`标志
- 所有演示都会在控制台输出详细的日志，请查看日志了解演示结果

## Java 17 主要新特性一览

| 版本 | 发布日期 | 主要特性 |
|------|---------|---------|
| Java 9 | 2017年9月 | 模块系统、JShell、集合工厂方法、Stream API增强、私有接口方法 |
| Java 10 | 2018年3月 | 局部变量类型推断(var)、垃圾收集器改进、应用类数据共享 |
| Java 11 | 2018年9月 | HTTP客户端标准化、String新方法、Lambda参数的局部变量语法 |
| Java 12 | 2019年3月 | Switch表达式(预览)、垃圾收集器改进、微基准测试套件 |
| Java 13 | 2019年9月 | 文本块(预览)、Switch表达式更新、动态CDS归档 |
| Java 14 | 2020年3月 | Switch表达式(标准)、文本块改进、空指针异常改进、Record(预览) |
| Java 15 | 2020年9月 | 文本块(标准)、密封类(预览)、隐藏类、Edwards-Curve数字签名算法 |
| Java 16 | 2021年3月 | Record(标准)、密封类(第二预览)、模式匹配for instanceof(标准)、Vector API |
| Java 17 | 2021年9月 | 密封类(标准)、模式匹配Switch(预览)、外部函数和内存API(孵化)、移除实验性AOT和JIT编译器 |

## 参考资料

- [JDK 17 Features](https://openjdk.java.net/projects/jdk/17/)
- [Java Language Changes for Java SE 17](https://docs.oracle.com/en/java/javase/17/language/java-language-changes.html)
- [JEP Index](https://openjdk.java.net/jeps/0)