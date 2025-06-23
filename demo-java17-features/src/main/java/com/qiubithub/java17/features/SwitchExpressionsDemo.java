package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Java 14 正式发布的Switch表达式演示
 */
@Slf4j
public class SwitchExpressionsDemo {

    // 枚举类型，用于演示
    public enum Size {
        SMALL, MEDIUM, LARGE, EXTRA_LARGE
    }

    // 演示方法
    public void demonstrate() {
        log.info("===== Switch表达式演示 =====");

        // 获取今天是星期几
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        log.info("今天是: {}", today);

        // 传统switch语句
        log.info("使用传统switch语句:");
        String typeOfDayTraditional = getTypeOfDayTraditional(today);
        log.info("  今天是{}", typeOfDayTraditional);

        // 新的switch表达式 - 使用箭头语法
        log.info("使用switch表达式(箭头语法):");
        String typeOfDayArrow = getTypeOfDayArrow(today);
        log.info("  今天是{}", typeOfDayArrow);

        // 新的switch表达式 - 使用yield关键字
        log.info("使用switch表达式(yield关键字):");
        String typeOfDayYield = getTypeOfDayYield(today);
        log.info("  今天是{}", typeOfDayYield);

        // 演示多个case标签
        log.info("演示多个case标签:");
        for (DayOfWeek day : DayOfWeek.values()) {
            log.info("  {} 是{}", day, getTypeOfDayMultipleLabels(day));
        }

        // 演示使用不同类型
        log.info("演示不同类型的switch表达式:");
        demonstrateSwitchWithDifferentTypes();
        
        // 演示模式匹配(预览特性，需要启用--enable-preview)
        log.info("演示switch中的模式匹配:");
        demonstrateSwitchPatternMatching();
    }

    // 传统switch语句
    private String getTypeOfDayTraditional(DayOfWeek day) {
        String typeOfDay;
        switch (day) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                typeOfDay = "工作日";
                break;
            case SATURDAY:
            case SUNDAY:
                typeOfDay = "周末";
                break;
            default:
                typeOfDay = "未知日";
                break;
        }
        return typeOfDay;
    }

    // 新的switch表达式 - 使用箭头语法
    private String getTypeOfDayArrow(DayOfWeek day) {
        String typeOfDay = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
            case SATURDAY, SUNDAY -> "周末";
            default -> "未知日";
        };
        return typeOfDay;
    }

    // 新的switch表达式 - 使用yield关键字
    private String getTypeOfDayYield(DayOfWeek day) {
        String typeOfDay = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY: {
                log.info("    这是工作日");
                yield "工作日";
            }
            case SATURDAY, SUNDAY: {
                log.info("    这是周末");
                yield "周末";
            }
            default: {
                log.info("    这是未知日");
                yield "未知日";
            }
        };
        return typeOfDay;
    }

    // 使用多个case标签
    private String getTypeOfDayMultipleLabels(DayOfWeek day) {
        return switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "工作日";
            case SATURDAY, SUNDAY -> "周末";
        };
    }

    // 演示使用不同类型的switch表达式
    private void demonstrateSwitchWithDifferentTypes() {
        // 使用枚举
        Size size = Size.MEDIUM;
        String sizeDescription = switch (size) {
            case SMALL -> "小号";
            case MEDIUM -> "中号";
            case LARGE -> "大号";
            case EXTRA_LARGE -> "特大号";
        };
        log.info("  尺寸 {} 描述为: {}", size, sizeDescription);

        // 使用字符串
        String fruit = "apple";
        String fruitDescription = switch (fruit.toLowerCase()) {
            case "apple" -> "苹果是红色或绿色的";
            case "banana" -> "香蕉是黄色的";
            case "orange" -> "橙子是橙色的";
            default -> "未知水果";
        };
        log.info("  水果 {} 描述为: {}", fruit, fruitDescription);

        // 使用整数
        int number = 2;
        String numberDescription = switch (number) {
            case 0 -> "零";
            case 1 -> "一";
            case 2 -> "二";
            default -> "其他数字";
        };
        log.info("  数字 {} 描述为: {}", number, numberDescription);
    }

    // 演示switch中的模式匹配(预览特性)
    private void demonstrateSwitchPatternMatching() {
        Object obj = "Hello";
        log.info("  对象: {}", obj);
        
        // 使用instanceof模式匹配
        if (obj instanceof String s) {
            log.info("  这是一个字符串，长度为: {}", s.length());
        } else if (obj instanceof Integer i) {
            log.info("  这是一个整数，值为: {}", i);
        } else {
            log.info("  这是其他类型的对象");
        }
        
        // 使用switch模式匹配(Java 17中是预览特性)
        // 注意：这需要启用--enable-preview编译器标志
        String result = formattedValue(obj);
        log.info("  格式化后的值: {}", result);
        
        // 测试几个不同类型
        testPatternMatchingSwitch("Hello World");
        testPatternMatchingSwitch(42);
        testPatternMatchingSwitch(42.5);
        testPatternMatchingSwitch(true);
        testPatternMatchingSwitch(null);
    }
    
    // 使用switch模式匹配格式化值(Java 17预览特性)
    private String formattedValue(Object obj) {
        return switch (obj) {
            case String s -> String.format("字符串: \"%s\"", s);
            case Integer i -> String.format("整数: %d", i);
            case Double d -> String.format("浮点数: %.2f", d);
            case Boolean b -> String.format("布尔值: %b", b);
            case null -> "null值";
            default -> obj.toString();
        };
    }
    
    // 测试模式匹配switch
    private void testPatternMatchingSwitch(Object obj) {
        log.info("  测试对象: {}, 结果: {}", obj, formattedValue(obj));
    }
}