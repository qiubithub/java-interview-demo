package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

/**
 * Java 14 引入的空指针异常改进演示
 * 增强的NullPointerException消息可以帮助开发者更快地定位问题
 */
@Slf4j
public class NullPointerExceptionImprovementDemo {

    // 用于演示的嵌套类
    public static class Person {
        private String name;
        private Address address;

        public Person(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public Address getAddress() {
            return address;
        }
    }

    public static class Address {
        private String street;
        private City city;

        public Address(String street, City city) {
            this.street = street;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public City getCity() {
            return city;
        }
    }

    public static class City {
        private String name;
        private String zipCode;

        public City(String name, String zipCode) {
            this.name = name;
            this.zipCode = zipCode;
        }

        public String getName() {
            return name;
        }

        public String getZipCode() {
            return zipCode;
        }
    }

    // 演示方法
    public void demonstrate() {
        log.info("===== 空指针异常改进演示 =====");

        try {
            // 创建一个有效的Person对象，但Address为null
            Person person = new Person("John Doe", null);
            
            // 尝试访问空Address的属性
            String street = person.getAddress().getStreet();
            log.info("街道: {}", street);
        } catch (NullPointerException e) {
            // 在Java 14之前，消息只是简单的"null"
            // 在Java 14之后，消息会指出哪个部分是null
            log.error("空指针异常: {}", e.getMessage());
        }

        try {
            // 创建一个有效的Person和Address对象，但City为null
            Person person = new Person("Jane Doe", new Address("123 Main St", null));
            
            // 尝试访问空City的属性
            String cityName = person.getAddress().getCity().getName();
            log.info("城市名称: {}", cityName);
        } catch (NullPointerException e) {
            // 消息会指出City是null
            log.error("空指针异常: {}", e.getMessage());
        }

        try {
            // 创建一个完整的对象链，但zipCode为null
            Person person = new Person(
                "Bob Smith", 
                new Address(
                    "456 Oak Ave", 
                    new City("Springfield", null)
                )
            );
            
            // 尝试在zipCode上调用方法
            int length = person.getAddress().getCity().getZipCode().length();
            log.info("邮编长度: {}", length);
        } catch (NullPointerException e) {
            // 消息会指出zipCode是null
            log.error("空指针异常: {}", e.getMessage());
        }

        try {
            // 创建一个数组，其中包含一个null元素
            String[] names = {"Alice", null, "Charlie"};
            
            // 尝试访问null元素的方法
            int length = names[1].length();
            log.info("名称长度: {}", length);
        } catch (NullPointerException e) {
            // 消息会指出数组中的哪个元素是null
            log.error("空指针异常: {}", e.getMessage());
        }

        try {
            // 使用null作为数组
            String[] nullArray = null;
            
            // 尝试访问null数组的元素
            String name = nullArray[0];
            log.info("名称: {}", name);
        } catch (NullPointerException e) {
            // 消息会指出数组本身是null
            log.error("空指针异常: {}", e.getMessage());
        }

        log.info("\n在Java 14之前，所有这些异常的消息都只是简单的'null'");
        log.info("在Java 14及以后，消息会明确指出哪个部分是null，大大提高了调试效率");
    }
}