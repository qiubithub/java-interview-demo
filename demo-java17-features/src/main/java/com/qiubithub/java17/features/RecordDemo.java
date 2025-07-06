package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

/**
 * Java 16 正式发布的Record类型演示
 * Record提供了一种简洁的方式来声明不可变的数据类
 */
@Slf4j
public class RecordDemo {

    // 定义一个简单的Record类型
    public record Person(String name, int age) {
        // 紧凑构造函数，可以添加验证逻辑
        public Person {
            if (age < 0) {
                throw new IllegalArgumentException("年龄不能为负数");
            }
        }

        // 可以添加静态方法
        public static Person createAdult(String name) {
            return new Person(name, 18);
        }

        // 可以添加实例方法
        public boolean isAdult() {
            return age >= 18;
        }
    }

    // 带泛型的Record
    public record Pair<T, U>(T first, U second) {
        public <V> V process(ProcessorFunction<T, U, V> processor) {
            return processor.process(first, second);
        }
    }

    // 函数式接口，用于处理Pair
    public interface ProcessorFunction<T, U, V> {
        V process(T t, U u);
    }

    // 嵌套Record
    public record Employee(String id, Person person, Department department) {
        public record Department(String name, String location) {
        }
    }

    // 演示方法
    public void demonstrate() {
        log.info("===== Record演示 =====");

        // 创建Record实例
        Person person = new Person("张三", 25);
        log.info("Person: {}", person);
        
        // 访问Record的字段
        log.info("姓名: {}, 年龄: {}", person.name(), person.age());
        
        // 使用Record的方法
        log.info("是否成年: {}", person.isAdult());
        
        // 使用静态工厂方法
        Person adult = Person.createAdult("李四");
        log.info("成年人: {}", adult);
        
        // Record的equals和hashCode
        Person anotherPerson = new Person("张三", 25);
        log.info("person.equals(anotherPerson): {}", person.equals(anotherPerson));
        log.info("person.hashCode() == anotherPerson.hashCode(): {}", person.hashCode() == anotherPerson.hashCode());
        
        // 泛型Record
        Pair<String, Integer> pair = new Pair<>("Hello", 42);
        String result = pair.process((s, i) -> s + i);
        log.info("处理结果: {}", result);
        
        // 嵌套Record
        Employee employee = new Employee(
            "E001", 
            new Person("王五", 30),
            new Employee.Department("研发部", "北京")
        );
        log.info("员工: {}", employee);
        log.info("员工部门: {}", employee.department());
        
        try {
            // 验证逻辑
            Person invalidPerson = new Person("无效", -1);
        } catch (IllegalArgumentException e) {
            log.info("验证异常: {}", e.getMessage());
        }
    }
}