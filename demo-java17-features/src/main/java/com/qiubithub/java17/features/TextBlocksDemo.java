package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

/**
 * Java 15 正式发布的文本块(Text Blocks)演示
 * 文本块提供了一种简洁的方式来表示多行字符串字面量
 */
@Slf4j
public class TextBlocksDemo {

    // 演示方法
    public void demonstrate() {
        log.info("===== 文本块演示 =====");

        // 传统多行字符串
        String traditionalJson = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"age\": 30,\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Anytown\",\n" +
                "    \"country\": \"USA\"\n" +
                "  },\n" +
                "  \"phoneNumbers\": [\n" +
                "    \"555-1234\",\n" +
                "    \"555-5678\"\n" +
                "  ]\n" +
                "}";
        
        // 使用文本块表示JSON
        String textBlockJson = """
                {
                  "name": "John Doe",
                  "age": 30,
                  "address": {
                    "street": "123 Main St",
                    "city": "Anytown",
                    "country": "USA"
                  },
                  "phoneNumbers": [
                    "555-1234",
                    "555-5678"
                  ]
                }
                """;
        
        log.info("传统JSON字符串长度: {}", traditionalJson.length());
        log.info("文本块JSON字符串长度: {}", textBlockJson.length());
        log.info("两者是否相等: {}", traditionalJson.equals(textBlockJson));
        
        // 使用文本块表示HTML
        String html = """
                <!DOCTYPE html>
                <html>
                  <head>
                    <title>文本块示例</title>
                  </head>
                  <body>
                    <h1>文本块让HTML在Java中更易读</h1>
                    <ul>
                      <li>不需要转义引号</li>
                      <li>保留格式</li>
                      <li>更少的噪音</li>
                    </ul>
                  </body>
                </html>
                """;
        
        log.info("HTML文本块:\n{}", html);
        
        // 文本块中的转义序列
        String escaped = """
                这是一行文本
                这是第二行，带有一个\t制表符
                这是第三行，带有一个\n换行符（不会生效，因为已经在文本块中）
                这是第四行，带有一个\\反斜杠
                """;
        
        log.info("带有转义序列的文本块:\n{}", escaped);
        
        // 使用 \ 连接行
        String concatenated = """
                这是一行很长的文本，\
                但在输出中它会显示为一行，\
                因为我们使用了行连接符。\
                """;
        
        log.info("使用行连接符的文本块: {}", concatenated);
        
        // 字符串插值（虽然Java没有原生支持，但可以使用String.format或格式化方法）
        String name = "Alice";
        int age = 25;
        
        String formatted = """
                用户信息:
                  姓名: %s
                  年龄: %d
                """.formatted(name, age);
        
        log.info("格式化的文本块:\n{}", formatted);
        
        // 控制缩进
        String indented = """
                这是第一级
                    这是第二级
                        这是第三级
                """;
        
        log.info("带缩进的文本块:\n{}", indented);
        
        // 使用String.indent方法进一步控制缩进
        String furtherIndented = indented.indent(4);
        
        log.info("进一步缩进的文本块:\n{}", furtherIndented);
    }
}