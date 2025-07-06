package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 10 引入的局部变量类型推断(var)演示
 */
@Slf4j
public class VarTypeInferenceDemo {

    // 演示方法
    public void demonstrate() {
        System.out.println("===== 局部变量类型推断(var)演示 =====");

        // 基本用法
        demonstrateBasicUsage();

        // 在循环中使用var
        demonstrateVarInLoops();

        // 在try-with-resources中使用var
        demonstrateVarInTryWithResources();

        // 在lambda表达式中使用var（Java 11特性）
        demonstrateVarInLambdas();

        // var的限制
        demonstrateVarLimitations();
    }

    // 基本用法
    private void demonstrateBasicUsage() {
        System.out.println("基本用法:");

        // 传统方式声明变量
        String traditionalString = "Hello";
        Integer traditionalInteger = 42;
        List<String> traditionalList = new ArrayList<>();
        Map<String, Integer> traditionalMap = new HashMap<>();

        // 使用var声明变量
        var inferredString = "Hello";
        var inferredInteger = 42;
        var inferredList = new ArrayList<String>();
        var inferredMap = new HashMap<String, Integer>();

        // 验证类型是否相同
        System.out.println("  传统String类型: " + traditionalString.getClass().getSimpleName() + 
                ", var推断类型: " + inferredString.getClass().getSimpleName());

        System.out.println("  传统Integer类型: " + traditionalInteger.getClass().getSimpleName() + 
                ", var推断类型: " + Integer.class.getSimpleName());

        System.out.println("  传统List类型: " + traditionalList.getClass().getSimpleName() + 
                ", var推断类型: " + inferredList.getClass().getSimpleName());

        System.out.println("  传统Map类型: " + traditionalMap.getClass().getSimpleName() + 
                ", var推断类型: " + inferredMap.getClass().getSimpleName());

        // 使用var可以简化复杂类型的声明
        Map<String, List<Map<Integer, String>>> complexMap = new HashMap<>();
        var simplifiedComplexMap = new HashMap<String, List<Map<Integer, String>>>();

        System.out.println("  复杂类型: " + complexMap.getClass().getSimpleName());
        System.out.println("  使用var的复杂类型: " + simplifiedComplexMap.getClass().getSimpleName());
    }

    // 在循环中使用var
    private void demonstrateVarInLoops() {
        System.out.println("在循环中使用var:");

        // 传统for循环
        System.out.println("  传统for循环:");
        for (int i = 0; i < 3; i++) {
            System.out.println("    索引: " + i);
        }

        // 使用var的for循环
        System.out.println("  使用var的for循环:");
        for (var i = 0; i < 3; i++) {
            System.out.println("    索引: " + i);
        }

        // 传统增强for循环
        List<String> items = List.of("Apple", "Banana", "Cherry");
        System.out.println("  传统增强for循环:");
        for (String item : items) {
            System.out.println("    项目: " + item);
        }

        // 使用var的增强for循环
        System.out.println("  使用var的增强for循环:");
        for (var item : items) {
            System.out.println("    项目: " + item);
        }

        // 在Map.Entry中使用var
        Map<String, Integer> scores = Map.of("Alice", 95, "Bob", 85, "Charlie", 90);
        System.out.println("  使用var遍历Map.Entry:");
        for (var entry : scores.entrySet()) {
            System.out.println("    " + entry.getKey() + " 得分: " + entry.getValue());
        }
    }

    // 在try-with-resources中使用var
    private void demonstrateVarInTryWithResources() {
        System.out.println("在try-with-resources中使用var:");

        String text = "Line 1\nLine 2\nLine 3";

        // 传统try-with-resources
        System.out.println("  传统try-with-resources:");
        try (BufferedReader traditionalReader = new BufferedReader(new StringReader(text))) {
            String line;
            while ((line = traditionalReader.readLine()) != null) {
                System.out.println("    读取: " + line);
            }
        } catch (IOException e) {
            System.err.println("读取错误: " + e.getMessage());
        }

        // 使用var的try-with-resources
        System.out.println("  使用var的try-with-resources:");
        try (var inferredReader = new BufferedReader(new StringReader(text))) {
            String line;
            while ((line = inferredReader.readLine()) != null) {
                System.out.println("    读取: " + line);
            }
        } catch (IOException e) {
            System.err.println("读取错误: " + e.getMessage());
        }
    }

    // 在lambda表达式中使用var（Java 11特性）
    private void demonstrateVarInLambdas() {
        System.out.println("在lambda表达式中使用var (Java 11特性):");

        // 传统lambda表达式
        Function<String, Integer> traditionalLambda = (String s) -> s.length();
        System.out.println("  传统lambda结果: " + traditionalLambda.apply("Hello"));

        // 使用var的lambda表达式
        Function<String, Integer> varLambda = (var s) -> s.length();
        System.out.println("  使用var的lambda结果: " + varLambda.apply("Hello"));

        // 带有多个参数的lambda表达式
        BiFunction<String, String, String> traditionalBiLambda = 
                (String s1, String s2) -> s1 + s2;
        System.out.println("  传统多参数lambda结果: " + traditionalBiLambda.apply("Hello, ", "World!"));

        // 使用var的多参数lambda表达式
        BiFunction<String, String, String> varBiLambda = 
                (var s1, var s2) -> s1 + s2;
        System.out.println("  使用var的多参数lambda结果: " + varBiLambda.apply("Hello, ", "World!"));

        // 使用var和注解的lambda表达式
        Function<List<String>, Integer> annotatedLambda = 
                (@NonNull var list) -> list.size();
        System.out.println("  带注解的var lambda结果: " + annotatedLambda.apply(List.of("a", "b", "c")));
    }

    // 模拟@NonNull注解
    private @interface NonNull {}

    // var的限制
    private void demonstrateVarLimitations() {
        System.out.println("var的限制:");

        // 1. 不能用于字段
        System.out.println("  1. 不能用于字段");
        // class Test { var field = "error"; }  // 编译错误

        // 2. 不能用于方法参数
        System.out.println("  2. 不能用于方法参数");
        // void method(var param) {}  // 编译错误

        // 3. 不能用于方法返回类型
        System.out.println("  3. 不能用于方法返回类型");
        // var method() { return "error"; }  // 编译错误

        // 4. 不能没有初始化器
        System.out.println("  4. 不能没有初始化器");
        // var noInitializer;  // 编译错误

        // 5. 不能初始化为null
        System.out.println("  5. 不能初始化为null");
        // var nullVar = null;  // 编译错误

        // 6. 不能使用数组初始化器
        System.out.println("  6. 不能使用数组初始化器");
        // var arr = { 1, 2, 3 };  // 编译错误
        
        // 正确的数组声明方式
        var arr = new int[] { 1, 2, 3 };
        System.out.println("    正确的数组声明: " + arr.getClass().getSimpleName());

        // 7. 不能在一条语句中声明多个变量
        System.out.println("  7. 不能在一条语句中声明多个变量");
        // var x = 1, y = 2;  // 编译错误

        // 8. Lambda表达式需要显式的目标类型
        System.out.println("  8. Lambda表达式需要显式的目标类型");
        // var lambda = () -> "error";  // 编译错误
        
        // 正确的Lambda声明方式
        var correctLambda = (Function<String, Integer>) (s -> s.length());
        System.out.println("    正确的Lambda声明: " + correctLambda.apply("test"));
    }
}