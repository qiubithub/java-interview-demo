package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

/**
 * Java 16 引入的instanceof模式匹配演示
 */
@Slf4j
public class PatternMatchingDemo {

    // 定义一些类用于演示
    public static abstract class Vehicle {
        private final String registrationNumber;

        public Vehicle(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }
    }

    public static class Car extends Vehicle {
        private final int numberOfSeats;

        public Car(String registrationNumber, int numberOfSeats) {
            super(registrationNumber);
            this.numberOfSeats = numberOfSeats;
        }

        public int getNumberOfSeats() {
            return numberOfSeats;
        }
    }

    public static class Truck extends Vehicle {
        private final double loadCapacity;

        public Truck(String registrationNumber, double loadCapacity) {
            super(registrationNumber);
            this.loadCapacity = loadCapacity;
        }

        public double getLoadCapacity() {
            return loadCapacity;
        }
    }

    public static class Motorcycle extends Vehicle {
        private final boolean hasSidecar;

        public Motorcycle(String registrationNumber, boolean hasSidecar) {
            super(registrationNumber);
            this.hasSidecar = hasSidecar;
        }

        public boolean hasSidecar() {
            return hasSidecar;
        }
    }

    // 演示方法
    public void demonstrate() {
        log.info("===== instanceof模式匹配演示 =====");

        // 创建不同类型的车辆
        Vehicle car = new Car("ABC123", 5);
        Vehicle truck = new Truck("XYZ789", 5000.0);
        Vehicle motorcycle = new Motorcycle("MNO456", true);

        // 使用模式匹配
        log.info("使用模式匹配处理车辆：");
        processVehicleWithPatternMatching(car);
        processVehicleWithPatternMatching(truck);
        processVehicleWithPatternMatching(motorcycle);

        // 传统方式比较
        log.info("\n传统方式处理车辆：");
        processVehicleTraditional(car);
        processVehicleTraditional(truck);
        processVehicleTraditional(motorcycle);

        // 在条件表达式中使用模式匹配
        log.info("\n在条件表达式中使用模式匹配：");
        printVehicleInfo(car);
        printVehicleInfo(truck);
        printVehicleInfo(motorcycle);
        printVehicleInfo(null);
    }

    // 使用instanceof模式匹配处理车辆
    private void processVehicleWithPatternMatching(Vehicle vehicle) {
        // 使用模式匹配，直接在instanceof中声明变量
        if (vehicle instanceof Car car) {
            log.info("这是一辆有{}个座位的汽车，注册号：{}", 
                    car.getNumberOfSeats(), car.getRegistrationNumber());
        } else if (vehicle instanceof Truck truck) {
            log.info("这是一辆载重能力为{}kg的卡车，注册号：{}", 
                    truck.getLoadCapacity(), truck.getRegistrationNumber());
        } else if (vehicle instanceof Motorcycle motorcycle) {
            log.info("这是一辆{}边车的摩托车，注册号：{}", 
                    motorcycle.hasSidecar() ? "带" : "不带", motorcycle.getRegistrationNumber());
        } else {
            log.info("未知车辆类型");
        }
    }

    // 传统方式处理车辆
    private void processVehicleTraditional(Vehicle vehicle) {
        // 传统方式，需要先检查类型，然后再强制转换
        if (vehicle instanceof Car) {
            Car car = (Car) vehicle;
            log.info("这是一辆有{}个座位的汽车，注册号：{}", 
                    car.getNumberOfSeats(), car.getRegistrationNumber());
        } else if (vehicle instanceof Truck) {
            Truck truck = (Truck) vehicle;
            log.info("这是一辆载重能力为{}kg的卡车，注册号：{}", 
                    truck.getLoadCapacity(), truck.getRegistrationNumber());
        } else if (vehicle instanceof Motorcycle) {
            Motorcycle motorcycle = (Motorcycle) vehicle;
            log.info("这是一辆{}边车的摩托车，注册号：{}", 
                    motorcycle.hasSidecar() ? "带" : "不带", motorcycle.getRegistrationNumber());
        } else {
            log.info("未知车辆类型");
        }
    }

    // 在条件表达式中使用模式匹配
    private void printVehicleInfo(Vehicle vehicle) {
        // 在条件表达式中使用模式匹配，避免嵌套if语句
        String info = (vehicle instanceof Car car) ? 
                        String.format("汽车，座位数：%d", car.getNumberOfSeats()) :
                      (vehicle instanceof Truck truck) ? 
                        String.format("卡车，载重：%.2fkg", truck.getLoadCapacity()) :
                      (vehicle instanceof Motorcycle motorcycle) ? 
                        String.format("摩托车，%s边车", motorcycle.hasSidecar() ? "带" : "不带") :
                        "未知车辆类型";
        
        log.info("车辆信息：{}", info);
    }
}