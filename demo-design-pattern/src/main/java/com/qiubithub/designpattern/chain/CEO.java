package com.qiubithub.designpattern.chain;

import java.math.BigDecimal;

/**
 * CEO审批者
 * 可以审批20000元以下的请求
 */
public class CEO extends Approver {
    
    /**
     * CEO的审批额度
     */
    private static final BigDecimal APPROVAL_LIMIT = new BigDecimal("20000");
    
    public CEO(String name) {
        super(name);
    }
    
    @Override
    public void processRequest(ApprovalRequest request) {
        // 如果请求已经被处理，则不再处理
        if (request.getStatus() != ApprovalRequest.RequestStatus.PENDING) {
            return;
        }
        
        // CEO可以审批20000元以下的请求
        if (request.getAmount().compareTo(APPROVAL_LIMIT) <= 0) {
            request.setStatus(ApprovalRequest.RequestStatus.APPROVED);
            logger.info("CEO " + name + " 审批通过了请求：" + request.getRequestId() + 
                    "，金额：" + request.getAmount() + "，类型：" + request.getRequestType());
        } else if (nextApprover != null) {
            // 超过审批额度，转交给下一个审批者
            logger.info("CEO " + name + " 无权审批请求：" + request.getRequestId() + 
                    "，金额：" + request.getAmount() + "，转交给下一级审批");
            nextApprover.processRequest(request);
        } else {
            // 没有下一个审批者，请求被拒绝
            request.setStatus(ApprovalRequest.RequestStatus.REJECTED);
            request.setRejectReason("超出CEO审批额度");
            logger.info("请求 " + request.getRequestId() + " 被拒绝，原因：" + request.getRejectReason());
        }
    }
}