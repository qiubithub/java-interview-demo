package com.qiubithub.java17.features;

import lombok.extern.slf4j.Slf4j;

/**
 * Java 17 正式发布的密封类(Sealed Classes)演示
 * 密封类限制了哪些类可以继承它，提供了比final更灵活的继承控制
 */
@Slf4j
public class SealedClassDemo {

    // 演示方法
    public void demonstrate() {
        System.out.println("===== 密封类演示 =====");

        // 创建各种形状
        Circle circle = new Circle("红色", 5);
        Rectangle rectangle = new Rectangle("蓝色", 4, 6);
        Triangle triangle = new Triangle("绿色", 3, 4, 5);
        EquilateralTriangle equilateralTriangle = new EquilateralTriangle("黄色", 5);
        RightTriangle rightTriangle = new RightTriangle("紫色", 3, 4);
        Square square = new Square("橙色", 4);

        // 输出形状信息
        System.out.println("圆形: " + circle + ", 面积: " + circle.area());
        System.out.println("矩形: " + rectangle + ", 面积: " + rectangle.area());
        System.out.println("三角形: " + triangle + ", 面积: " + triangle.area());
        System.out.println("等边三角形: " + equilateralTriangle + ", 面积: " + equilateralTriangle.area());
        System.out.println("直角三角形: " + rightTriangle + ", 面积: " + rightTriangle.area());
        System.out.println("正方形: " + square + ", 面积: " + square.area());

        // 使用模式匹配进行类型检查和转换
        Shape[] shapes = {circle, rectangle, triangle, equilateralTriangle, rightTriangle, square};
        for (Shape shape : shapes) {
            processShape(shape);
        }
    }

    // 使用instanceof模式匹配处理不同形状
    private void processShape(Shape shape) {
        if (shape instanceof Circle c) {
            System.out.println("处理圆形: 半径 = " + c.radius);
        } else if (shape instanceof Rectangle r) {
            System.out.println("处理矩形: 宽 = " + r.width + ", 高 = " + r.height);
            if (r instanceof Square s) {
                System.out.println("  这是一个正方形: 边长 = " + s.width);
            }
        } else if (shape instanceof Triangle t) {
            System.out.println("处理三角形: 边长 = [" + t.a + ", " + t.b + ", " + t.c + "]");
            if (t instanceof EquilateralTriangle et) {
                System.out.println("  这是一个等边三角形: 边长 = " + et.a);
            } else if (t instanceof RightTriangle rt) {
                System.out.println("  这是一个直角三角形: 直角边 = [" + rt.a + ", " + rt.b + "]");
            }
        }
    }
}

// 密封抽象类，只允许特定的子类继承
abstract sealed class Shape permits Circle, Rectangle, Triangle {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double area();

    public String getColor() {
        return color;
    }
}

// 密封类的子类之一，使用final修饰，不允许进一步继承
final class Circle extends Shape {
    protected final double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public String toString() {
        return "Circle[color=" + color + ", radius=" + radius + "]";
    }
}

// 密封类的子类之一，使用non-sealed修饰，允许任意继承
non-sealed class Rectangle extends Shape {
    protected final double width;
    protected final double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public String toString() {
        return "Rectangle[color=" + color + ", width=" + width + ", height=" + height + "]";
    }
}

// 密封类的子类之一，使用sealed修饰，继续限制继承
sealed class Triangle extends Shape permits EquilateralTriangle, RightTriangle {
    protected final double a;
    protected final double b;
    protected final double c;

    public Triangle(String color, double a, double b, double c) {
        super(color);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double area() {
        // 海伦公式
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override
    public String toString() {
        return "Triangle[color=" + color + ", sides=[" + a + ", " + b + ", " + c + "]]";
    }
}

// 密封Triangle的子类之一
final class EquilateralTriangle extends Triangle {
    public EquilateralTriangle(String color, double side) {
        super(color, side, side, side);
    }

    @Override
    public String toString() {
        return "EquilateralTriangle[color=" + color + ", side=" + a + "]";
    }
}

// 密封Triangle的子类之二
final class RightTriangle extends Triangle {
    public RightTriangle(String color, double a, double b) {
        super(color, a, b, Math.sqrt(a * a + b * b));
    }

    @Override
    public String toString() {
        return "RightTriangle[color=" + color + ", a=" + a + ", b=" + b + "]";
    }
}

// Rectangle的非密封子类
class Square extends Rectangle {
    public Square(String color, double side) {
        super(color, side, side);
    }

    @Override
    public String toString() {
        return "Square[color=" + color + ", side=" + width + "]";
    }
}