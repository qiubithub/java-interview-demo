package com.qiubithub.java17.controller;

import com.qiubithub.java17.features.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/java17")
public class Java17DemoController {

    private final RecordDemo recordDemo = new RecordDemo();
    private final SealedClassDemo sealedClassDemo = new SealedClassDemo();
    private final PatternMatchingDemo patternMatchingDemo = new PatternMatchingDemo();
    private final TextBlocksDemo textBlocksDemo = new TextBlocksDemo();
    private final SwitchExpressionsDemo switchExpressionsDemo = new SwitchExpressionsDemo();
    private final StreamApiEnhancementsDemo streamApiEnhancementsDemo = new StreamApiEnhancementsDemo();
    private final VarTypeInferenceDemo varTypeInferenceDemo = new VarTypeInferenceDemo();
    private final NullPointerExceptionImprovementDemo nullPointerExceptionImprovementDemo = new NullPointerExceptionImprovementDemo();

    /**
     * 列出所有可用的演示
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> listDemos() {
        Map<String, String> demos = new HashMap<>();
        demos.put("record", "Record类型演示");
        demos.put("sealed", "密封类演示");
        demos.put("pattern-matching", "模式匹配演示");
        demos.put("text-blocks", "文本块演示");
        demos.put("switch-expressions", "Switch表达式演示");
        demos.put("stream-api", "Stream API增强演示");
        demos.put("var-type", "局部变量类型推断演示");
        demos.put("npe-improvement", "空指针异常改进演示");
        demos.put("all", "运行所有演示");
        
        return ResponseEntity.ok(demos);
    }

    /**
     * 运行指定的演示
     */
    @GetMapping("/{demo}")
    public ResponseEntity<Map<String, Object>> runDemo(@PathVariable String demo) {
        Map<String, Object> result = new HashMap<>();
        result.put("demo", demo);
        
        try {
            switch (demo) {
                case "record" -> {
                    log.info("运行Record类型演示");
                    recordDemo.demonstrate();
                    result.put("message", "Record类型演示已运行，请查看日志输出");
                }
                case "sealed" -> {
                    log.info("运行密封类演示");
                    sealedClassDemo.demonstrate();
                    result.put("message", "密封类演示已运行，请查看日志输出");
                }
                case "pattern-matching" -> {
                    log.info("运行模式匹配演示");
                    patternMatchingDemo.demonstrate();
                    result.put("message", "模式匹配演示已运行，请查看日志输出");
                }
                case "text-blocks" -> {
                    log.info("运行文本块演示");
                    textBlocksDemo.demonstrate();
                    result.put("message", "文本块演示已运行，请查看日志输出");
                }
                case "switch-expressions" -> {
                    log.info("运行Switch表达式演示");
                    switchExpressionsDemo.demonstrate();
                    result.put("message", "Switch表达式演示已运行，请查看日志输出");
                }
                case "stream-api" -> {
                    log.info("运行Stream API增强演示");
                    streamApiEnhancementsDemo.demonstrate();
                    result.put("message", "Stream API增强演示已运行，请查看日志输出");
                }
                case "var-type" -> {
                    log.info("运行局部变量类型推断演示");
                    varTypeInferenceDemo.demonstrate();
                    result.put("message", "局部变量类型推断演示已运行，请查看日志输出");
                }
                case "npe-improvement" -> {
                    log.info("运行空指针异常改进演示");
                    nullPointerExceptionImprovementDemo.demonstrate();
                    result.put("message", "空指针异常改进演示已运行，请查看日志输出");
                }
                case "all" -> {
                    log.info("运行所有演示");
                    recordDemo.demonstrate();
                    sealedClassDemo.demonstrate();
                    patternMatchingDemo.demonstrate();
                    textBlocksDemo.demonstrate();
                    switchExpressionsDemo.demonstrate();
                    streamApiEnhancementsDemo.demonstrate();
                    varTypeInferenceDemo.demonstrate();
                    nullPointerExceptionImprovementDemo.demonstrate();
                    result.put("message", "所有演示已运行，请查看日志输出");
                }
                default -> {
                    result.put("error", "未知的演示: " + demo);
                    return ResponseEntity.badRequest().body(result);
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("error", "演示运行出错: " + e.getMessage());
            log.error("演示运行出错", e);
            return ResponseEntity.internalServerError().body(result);
        }
    }
}