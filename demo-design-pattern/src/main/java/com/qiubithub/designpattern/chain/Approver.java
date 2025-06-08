package com.qiubithub.designpattern.chain;

import java.util.logging.Logger;

/**
 * 审批者抽象类
 * 
 * 责任链模式的优点：
 * 1. 降低耦合度，一个对象无需知道是哪个对象处理其请求
 * 2. 增强给对象指派职责的灵活性
 * 3. 可以动态地添加或删除责任
 * 
 * 责任链模式的缺点：
 * 1. 不能保证请求一定被处理
 * 2. 系统性能会受到影响，特别是在链条很长的时候
 * 3. 可能不容易观察运行时的特征，有碍于除错
 */
public abstract class Approver {
    
    protected static final Logger logger = Logger.getLogger(Approver.class.getName());
    
    /**
     * 审批者姓名
     */
    protected String name;
    
    /**
     * 下一个处理者
     */
    protected Approver nextApprover;
    
    public Approver(String name) {
        this.name = name;
    }
    
    /**
     * 设置下一个处理者
     * 
     * @param nextApprover 下一个处理者
     * @return 下一个处理者，便于链式调用
     */
    public Approver setNextApprover(Approver nextApprover) {
        this.nextApprover = nextApprover;
        return nextApprover;
    }
    
    /**
     * 处理审批请求
     * 
     * @param request 审批请求
     */
    public abstract void processRequest(ApprovalRequest request);
}