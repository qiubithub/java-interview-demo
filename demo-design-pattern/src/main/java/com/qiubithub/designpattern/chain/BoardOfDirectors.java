package com.qiubithub.designpattern.chain;

import java.math.BigDecimal;

/**
 * 董事会审批者
 * 可以审批任意金额的请求
 */
public class BoardOfDirectors extends Approver {
    
    public BoardOfDirectors(String name) {
        super(name);
    }
    
    @Override
    public void processRequest(ApprovalRequest request) {
        // 如果请求已经被处理，则不再处理
        if (request.getStatus() != ApprovalRequest.RequestStatus.PENDING) {
            return;
        }
        
        // 董事会可以审批任意金额的请求
        request.setStatus(ApprovalRequest.RequestStatus.APPROVED);
        logger.info("董事会 " + name + " 审批通过了请求：" + request.getRequestId() + 
                "，金额：" + request.getAmount() + "，类型：" + request.getRequestType());
    }
}