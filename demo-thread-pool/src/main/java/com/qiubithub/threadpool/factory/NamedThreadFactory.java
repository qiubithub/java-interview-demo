package com.qiubithub.threadpool.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 自定义命名线程工厂
 * </p>
 *
 * @author qiuchuanze
 * @date 2023/11/01
 */
public class NamedThreadFactory implements ThreadFactory {

    /**
     * 线程组
     */
    private final ThreadGroup group;
    
    /**
     * 线程数量
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    
    /**
     * 线程名前缀
     */
    private final String namePrefix;
    
    /**
     * 是否守护线程
     */
    private final boolean daemon;

    /**
     * 构造函数
     *
     * @param namePrefix 线程名前缀
     * @param daemon     是否守护线程
     */
    public NamedThreadFactory(String namePrefix, boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    /**
     * 构造函数，默认为非守护线程
     *
     * @param namePrefix 线程名前缀
     */
    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "-" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        // 设置默认优先级
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}