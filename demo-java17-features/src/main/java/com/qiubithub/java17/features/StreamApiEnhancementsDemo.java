package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 9-17 中Stream API的增强演示
 */
@Slf4j
public class StreamApiEnhancementsDemo {

    // 演示方法
    public void demonstrate() {
        log.info("===== Stream API增强演示 =====");

        // Java 9: Stream.ofNullable
        demonstrateStreamOfNullable();

        // Java 9: Stream.iterate 重载方法
        demonstrateStreamIterate();

        // Java 9: takeWhile 和 dropWhile
        demonstrateTakeWhileDropWhile();

        // Java 10: 集合到不可变集合的转换
        demonstrateToUnmodifiableCollectors();

        // Java 12: teeing 收集器
        demonstrateTeeing();

        // Java 16: Stream.toList 便捷方法
        demonstrateStreamToList();
    }

    // Java 9: Stream.ofNullable
    private void demonstrateStreamOfNullable() {
        log.info("Java 9: Stream.ofNullable");

        // 处理可能为null的值
        String nullableValue = null;
        
        // 旧方式：需要进行null检查
        Stream<String> oldWay = nullableValue == null ? Stream.empty() : Stream.of(nullableValue);
        log.info("  旧方式结果数量: {}", oldWay.count());
        
        // 新方式：使用ofNullable
        Stream<String> newWay = Stream.ofNullable(nullableValue);
        log.info("  新方式结果数量: {}", newWay.count());
        
        // 使用非null值
        String nonNullValue = "Hello";
        Stream<String> nonNullStream = Stream.ofNullable(nonNullValue);
        log.info("  非null值结果: {}", nonNullStream.findFirst().orElse("Empty"));
    }

    // Java 9: Stream.iterate 重载方法
    private void demonstrateStreamIterate() {
        log.info("Java 9: Stream.iterate 重载方法");

        // 旧方式：无限流，需要使用limit
        Stream<Integer> oldWay = Stream.iterate(0, n -> n + 2).limit(5);
        log.info("  旧方式(无限流+limit)结果: {}", oldWay.collect(Collectors.toList()));
        
        // 新方式：带有谓词的iterate，可以创建有限流
        Stream<Integer> newWay = Stream.iterate(0, n -> n < 10, n -> n + 2);
        log.info("  新方式(带谓词的有限流)结果: {}", newWay.collect(Collectors.toList()));
        
        // 使用实例：生成斐波那契数列的前10个数
        Stream<int[]> fibStream = Stream.iterate(
            new int[]{0, 1}, 
            fib -> fib[0] + fib[1] <= 100,  // 谓词：只要不超过100
            fib -> new int[]{fib[1], fib[0] + fib[1]} // 下一个斐波那契数对
        );
        
        List<Integer> fibNumbers = fibStream
            .map(fib -> fib[0])
            .collect(Collectors.toList());
            
        log.info("  斐波那契数列(不超过100): {}", fibNumbers);
    }

    // Java 9: takeWhile 和 dropWhile
    private void demonstrateTakeWhileDropWhile() {
        log.info("Java 9: takeWhile 和 dropWhile");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // takeWhile: 从流的开头获取元素，直到谓词返回false
        List<Integer> takeWhileResult = numbers.stream()
            .takeWhile(n -> n < 5)
            .collect(Collectors.toList());
        log.info("  takeWhile(n < 5)结果: {}", takeWhileResult);
        
        // dropWhile: 从流中删除元素，直到谓词返回false，然后返回剩余元素
        List<Integer> dropWhileResult = numbers.stream()
            .dropWhile(n -> n < 5)
            .collect(Collectors.toList());
        log.info("  dropWhile(n < 5)结果: {}", dropWhileResult);
        
        // 使用实例：处理有序数据
        List<String> words = Arrays.asList("a", "b", "c", "d", "e", "apple", "banana", "cherry");
        
        List<String> shortWords = words.stream()
            .takeWhile(w -> w.length() == 1)
            .collect(Collectors.toList());
        log.info("  takeWhile(单字母单词)结果: {}", shortWords);
        
        List<String> longWords = words.stream()
            .dropWhile(w -> w.length() == 1)
            .collect(Collectors.toList());
        log.info("  dropWhile(单字母单词)结果: {}", longWords);
    }

    // Java 10: 集合到不可变集合的转换
    private void demonstrateToUnmodifiableCollectors() {
        log.info("Java 10: 集合到不可变集合的转换");

        List<String> fruits = Arrays.asList("apple", "banana", "cherry", "date");
        
        // 旧方式：创建不可修改的列表
        List<String> oldWayList = Collections.unmodifiableList(fruits);
        
        // 新方式：使用toUnmodifiableList收集器
        List<String> unmodifiableList = fruits.stream()
            .collect(Collectors.toUnmodifiableList());
        log.info("  不可变列表: {}", unmodifiableList);
        
        // 尝试修改不可变列表会抛出UnsupportedOperationException
        try {
            unmodifiableList.add("elderberry");
        } catch (UnsupportedOperationException e) {
            log.info("  尝试修改不可变列表时抛出异常: {}", e.getClass().getSimpleName());
        }
        
        // 其他不可变收集器
        var unmodifiableSet = fruits.stream()
            .collect(Collectors.toUnmodifiableSet());
        log.info("  不可变集合: {}", unmodifiableSet);
        
        var unmodifiableMap = fruits.stream()
            .collect(Collectors.toUnmodifiableMap(
                s -> s.substring(0, 1),  // 键: 首字母
                s -> s                   // 值: 完整单词
            ));
        log.info("  不可变映射: {}", unmodifiableMap);
    }

    // 添加Collections类的导入
    private static class Collections {
        public static <T> List<T> unmodifiableList(List<T> list) {
            return List.copyOf(list);
        }
    }

    // Java 12: teeing 收集器
    private void demonstrateTeeing() {
        log.info("Java 12: teeing 收集器");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 使用teeing同时计算平均值和总和
        record SummaryStats(double average, int sum) {}
        
        SummaryStats stats = numbers.stream().collect(
            Collectors.teeing(
                Collectors.averagingInt(i -> i),     // 第一个收集器：计算平均值
                Collectors.summingInt(i -> i),       // 第二个收集器：计算总和
                SummaryStats::new                    // 合并函数：创建结果对象
            )
        );
        
        log.info("  平均值: {}, 总和: {}", stats.average(), stats.sum());
        
        // 使用teeing找出最小值和最大值
        record MinMax(int min, int max) {}
        
        MinMax minMax = numbers.stream().collect(
            Collectors.teeing(
                Collectors.minBy(Integer::compare),  // 第一个收集器：找最小值
                Collectors.maxBy(Integer::compare),  // 第二个收集器：找最大值
                (min, max) -> new MinMax(
                    min.orElse(0), 
                    max.orElse(0)
                )                                   // 合并函数：创建结果对象
            )
        );
        
        log.info("  最小值: {}, 最大值: {}", minMax.min(), minMax.max());
        
        // 使用teeing将元素分为两组
        record EvenOdd(List<Integer> evens, List<Integer> odds) {}
        
        EvenOdd evenOdd = numbers.stream().collect(
            Collectors.teeing(
                Collectors.filtering(n -> n % 2 == 0, Collectors.toList()),  // 偶数
                Collectors.filtering(n -> n % 2 != 0, Collectors.toList()),  // 奇数
                EvenOdd::new                                                // 合并
            )
        );
        
        log.info("  偶数: {}, 奇数: {}", evenOdd.evens(), evenOdd.odds());
    }

    // Java 16: Stream.toList 便捷方法
    private void demonstrateStreamToList() {
        log.info("Java 16: Stream.toList 便捷方法");

        List<String> fruits = Arrays.asList("apple", "banana", "cherry", "date");
        
        // 旧方式：使用collect(Collectors.toList())
        List<String> oldWay = fruits.stream()
            .filter(s -> s.length() > 5)
            .collect(Collectors.toList());
        log.info("  旧方式结果: {}", oldWay);
        
        // 新方式：使用toList()
        List<String> newWay = fruits.stream()
            .filter(s -> s.length() > 5)
            .toList();
        log.info("  新方式结果: {}", newWay);
        
        // 注意：toList()返回的是不可修改的列表
        try {
            newWay.add("elderberry");
        } catch (UnsupportedOperationException e) {
            log.info("  尝试修改toList()结果时抛出异常: {}", e.getClass().getSimpleName());
        }
    }
}