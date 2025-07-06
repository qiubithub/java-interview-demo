package com.qiubithub.designpattern.chain;

import java.math.BigDecimal;

/**
 * 审批请求类
 */
public class ApprovalRequest {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 请求类型
     */
    private RequestType requestType;
    
    /**
     * 请求金额
     */
    private BigDecimal amount;
    
    /**
     * 请求描述
     */
    private String description;
    
    /**
     * 请求状态
     */
    private RequestStatus status = RequestStatus.PENDING;
    
    /**
     * 拒绝原因
     */
    private String rejectReason;
    
    /**
     * 构造函数
     * 
     * @param requestId 请求ID
     * @param requestType 请求类型
     * @param amount 请求金额
     * @param description 请求描述
     */
    public ApprovalRequest(String requestId, RequestType requestType, BigDecimal amount, String description) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.amount = amount;
        this.description = description;
    }    
    public String getRequestId() {
        return requestId;
    }
    
    public RequestType getRequestType() {
        return requestType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    /**
     * 请求类型枚举
     */
    public enum RequestType {
        /**
         * 报销请求
         */
        EXPENSE,
        
        /**
         * 请假请求
         */
        LEAVE,
        
        /**
         * 采购请求
         */
        PURCHASE
    }    
    /**
     * 请求状态枚举
     */
    public enum RequestStatus {
        /**
         * 待审批
         */
        PENDING,
        
        /**
         * 已批准
         */
        APPROVED,
        
        /**
         * 已拒绝
         */
        REJECTED
    }
}