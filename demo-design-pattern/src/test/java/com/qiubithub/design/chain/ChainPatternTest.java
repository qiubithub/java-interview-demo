package com.qiubithub.designpattern.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 责任链模式测试类
 */
public class ChainPatternTest {
    
    @Test
    @DisplayName("测试责任链模式 - 小额请求由团队领导处理")
    public void testSmallAmountRequest() {
        // 创建责任链
        Approver teamLeader = new TeamLeader("张三");
        Approver departmentManager = new DepartmentManager("李四");
        Approver ceo = new CEO("王五");
        Approver boardOfDirectors = new BoardOfDirectors("董事会");
        
        // 设置责任链顺序
        teamLeader.setNextApprover(departmentManager);
        departmentManager.setNextApprover(ceo);
        ceo.setNextApprover(boardOfDirectors);
        
        // 创建一个小额请求 (800元)
        ApprovalRequest smallRequest = new ApprovalRequest(
                "REQ-001", 
                ApprovalRequest.RequestType.EXPENSE, 
                new BigDecimal("800"), 
                "团建活动费用");
        
        // 处理请求
        teamLeader.processRequest(smallRequest);
        
        // 验证请求状态
        assertEquals(ApprovalRequest.RequestStatus.APPROVED, smallRequest.getStatus(), "小额请求应被团队领导批准");
    }    
    @Test
    @DisplayName("测试责任链模式 - 中额请求由部门经理处理")
    public void testMediumAmountRequest() {
        // 创建责任链
        Approver teamLeader = new TeamLeader("张三");
        Approver departmentManager = new DepartmentManager("李四");
        Approver ceo = new CEO("王五");
        
        // 设置责任链顺序
        teamLeader.setNextApprover(departmentManager);
        departmentManager.setNextApprover(ceo);
        
        // 创建一个中额请求 (3000元)
        ApprovalRequest mediumRequest = new ApprovalRequest(
                "REQ-002", 
                ApprovalRequest.RequestType.EXPENSE, 
                new BigDecimal("3000"), 
                "部门设备采购");
        
        // 处理请求
        teamLeader.processRequest(mediumRequest);
        
        // 验证请求状态
        assertEquals(ApprovalRequest.RequestStatus.APPROVED, mediumRequest.getStatus(), "中额请求应被部门经理批准");
    }
    
    @Test
    @DisplayName("测试责任链模式 - 大额请求由CEO处理")
    public void testLargeAmountRequest() {
        // 创建责任链
        Approver teamLeader = new TeamLeader("张三");
        Approver departmentManager = new DepartmentManager("李四");
        Approver ceo = new CEO("王五");
        
        // 设置责任链顺序
        teamLeader.setNextApprover(departmentManager);
        departmentManager.setNextApprover(ceo);
        
        // 创建一个大额请求 (15000元)
        ApprovalRequest largeRequest = new ApprovalRequest(
                "REQ-003", 
                ApprovalRequest.RequestType.PURCHASE, 
                new BigDecimal("15000"), 
                "服务器设备采购");
        
        // 处理请求
        teamLeader.processRequest(largeRequest);
        
        // 验证请求状态
        assertEquals(ApprovalRequest.RequestStatus.APPROVED, largeRequest.getStatus(), "大额请求应被CEO批准");
    }    
    @Test
    @DisplayName("测试责任链模式 - 超大额请求由董事会处理")
    public void testExtraLargeAmountRequest() {
        // 创建责任链
        Approver teamLeader = new TeamLeader("张三");
        Approver departmentManager = new DepartmentManager("李四");
        Approver ceo = new CEO("王五");
        Approver boardOfDirectors = new BoardOfDirectors("董事会");
        
        // 设置责任链顺序
        teamLeader.setNextApprover(departmentManager);
        departmentManager.setNextApprover(ceo);
        ceo.setNextApprover(boardOfDirectors);
        
        // 创建一个超大额请求 (50000元)
        ApprovalRequest extraLargeRequest = new ApprovalRequest(
                "REQ-004", 
                ApprovalRequest.RequestType.PURCHASE, 
                new BigDecimal("50000"), 
                "公司年度设备更新");
        
        // 处理请求
        teamLeader.processRequest(extraLargeRequest);
        
        // 验证请求状态
        assertEquals(ApprovalRequest.RequestStatus.APPROVED, extraLargeRequest.getStatus(), "超大额请求应被董事会批准");
    }    
    @Test
    @DisplayName("测试责任链模式 - 没有合适处理者的请求被拒绝")
    public void testRejectedRequest() {
        // 创建责任链，但没有董事会
        Approver teamLeader = new TeamLeader("张三");
        Approver departmentManager = new DepartmentManager("李四");
        Approver ceo = new CEO("王五");
        
        // 设置责任链顺序
        teamLeader.setNextApprover(departmentManager);
        departmentManager.setNextApprover(ceo);
        
        // 创建一个超大额请求 (50000元)
        ApprovalRequest extraLargeRequest = new ApprovalRequest(
                "REQ-005", 
                ApprovalRequest.RequestType.PURCHASE, 
                new BigDecimal("50000"), 
                "公司年度设备更新");
        
        // 处理请求
        teamLeader.processRequest(extraLargeRequest);
        
        // 验证请求状态
        assertEquals(ApprovalRequest.RequestStatus.REJECTED, extraLargeRequest.getStatus(), "无合适处理者的请求应被拒绝");
        assertNotNull(extraLargeRequest.getRejectReason(), "拒绝的请求应有拒绝原因");
    }
}