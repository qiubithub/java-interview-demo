package com.qiubithub.langchain4j.tool;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JavaCodeExampleTool {

    private final Map<String, String> codeExamples;

    public JavaCodeExampleTool() {
        this.codeExamples = initializeCodeExamples();
    }

    @Tool(name = "getJavaCodeExample")
    public String getJavaCodeExample(String concept) {
        
        log.info("获取Java代码示例: {}", concept);
        
        String normalizedConcept = concept.toLowerCase().trim();
        
        if (codeExamples.containsKey(normalizedConcept)) {
            return codeExamples.get(normalizedConcept);
        } else {
            // 尝试部分匹配
            for (Map.Entry<String, String> entry : codeExamples.entrySet()) {
                if (entry.getKey().contains(normalizedConcept) || normalizedConcept.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        
        return "抱歉，我没有关于 " + concept + " 的代码示例。请尝试其他概念，如 'singleton'（单例模式）、'stream api'（流式API）、'lambda'（lambda表达式）、'thread'（线程）或 'spring boot controller'（Spring Boot控制器）。";
    }    private Map<String, String> initializeCodeExamples() {
        Map<String, String> examples = new HashMap<>();
        
        // Singleton Pattern
        examples.put("singleton", """
            // 线程安全的单例模式，采用延迟初始化
            public class Singleton {
                private static volatile Singleton instance;
                
                private Singleton() {}
                
                public static Singleton getInstance() {
                    if (instance == null) {
                        synchronized (Singleton.class) {
                            if (instance == null) {
                                instance = new Singleton();
                            }
                        }
                    }
                    return instance;
                }
            }
            """);
        
        // Stream API
        examples.put("stream api", """
            // Stream API 示例
            import java.util.Arrays;
            import java.util.List;
            import java.util.stream.Collectors;
            
            public class StreamExample {
                public static void main(String[] args) {
                    List<String> names = Arrays.asList("John", "Jane", "Adam", "Tom", "Alice");
                    
                    // 过滤以'J'开头的名字
                    List<String> filteredNames = names.stream()
                            .filter(name -> name.startsWith("J"))
                            .collect(Collectors.toList());
                    
                    // 转换为大写并用逗号连接
                    String result = names.stream()
                            .map(String::toUpperCase)
                            .collect(Collectors.joining(", "));
                    
                    // 查找长度大于4的任意名字
                    boolean anyLongName = names.stream()
                            .anyMatch(name -> name.length() > 4);
                }
            }
            """);        
        // Lambda Expression
        examples.put("lambda", """
            // Lambda表达式示例
            import java.util.Arrays;
            import java.util.List;
            import java.util.function.Consumer;
            import java.util.function.Function;
            import java.util.function.Predicate;
            
            public class LambdaExample {
                public static void main(String[] args) {
                    // 简单的lambda表达式
                    Runnable runnable = () -> System.out.println("你好，Lambda！");
                    
                    // 带参数的lambda表达式
                    Consumer<String> consumer = (String s) -> System.out.println(s);
                    
                    // 类型推断的lambda表达式
                    Consumer<String> betterConsumer = s -> System.out.println(s);
                    
                    // Predicate示例
                    Predicate<String> predicate = s -> s.length() > 5;
                    
                    // Function示例
                    Function<String, Integer> function = s -> s.length();
                    
                    // 在集合中使用lambda表达式
                    List<String> names = Arrays.asList("John", "Jane", "Adam");
                    names.forEach(name -> System.out.println("你好，" + name));
                }
            }
            """);        
        // Multithreading
        examples.put("thread", """
            // 多线程示例
            import java.util.concurrent.ExecutorService;
            import java.util.concurrent.Executors;
            import java.util.concurrent.Future;
            
            public class ThreadExample {
                public static void main(String[] args) {
                    // 通过继承Thread类创建线程
                    Thread thread1 = new MyThread();
                    thread1.start();
                    
                    // 通过实现Runnable接口创建线程
                    Thread thread2 = new Thread(new MyRunnable());
                    thread2.start();
                    
                    // 使用lambda表达式创建线程
                    Thread thread3 = new Thread(() -> {
                        System.out.println("使用lambda表达式运行的线程");
                    });
                    thread3.start();
                    
                    // 使用ExecutorService
                    ExecutorService executor = Executors.newFixedThreadPool(2);
                    Future<?> future = executor.submit(() -> {
                        System.out.println("由ExecutorService执行的任务");
                    });
                    
                    executor.shutdown();
                }
                
                static class MyThread extends Thread {
                    @Override
                    public void run() {
                        System.out.println("通过继承Thread类运行的线程");
                    }
                }
                
                static class MyRunnable implements Runnable {
                    @Override
                    public void run() {
                        System.out.println("通过实现Runnable接口运行的线程");
                    }
                }
            }
            """);        
        // Spring Boot Controller
        examples.put("spring boot controller", """
            // Spring Boot REST控制器示例
            import org.springframework.http.ResponseEntity;
            import org.springframework.web.bind.annotation.*;
            
            import java.util.List;
            
            @RestController
            @RequestMapping("/api/users")
            public class UserController {
                
                private final UserService userService;
                
                public UserController(UserService userService) {
                    this.userService = userService;
                }
                
                @GetMapping
                public List<User> getAllUsers() {
                    return userService.findAll();
                }
                
                @GetMapping("/{id}")
                public ResponseEntity<User> getUserById(@PathVariable Long id) {
                    return userService.findById(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                }
                
                @PostMapping
                public ResponseEntity<User> createUser(@RequestBody User user) {
                    User savedUser = userService.save(user);
                    return ResponseEntity.ok(savedUser);
                }
                
                @PutMapping("/{id}")
                public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
                    user.setId(id);
                    User updatedUser = userService.update(user);
                    return ResponseEntity.ok(updatedUser);
                }
                
                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
                    userService.deleteById(id);
                    return ResponseEntity.noContent().build();
                }
            }
            """);
        
        return examples;
    }
}