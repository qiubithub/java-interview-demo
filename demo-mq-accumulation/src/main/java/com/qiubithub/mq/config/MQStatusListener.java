package com.qiubithub.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MQStatusListener {

    @Value("${app.mq.enabled:false}")
    private boolean mqEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (mqEnabled) {
            log.info("==========================================");
            log.info("RocketMQ功能已启用");
            log.info("可以使用 /api/messages/* 接口测试RocketMQ消息堆积处理");
            log.info("==========================================");
        } else {
            log.info("==========================================");
            log.info("RocketMQ功能已禁用");
            log.info("请使用 /api/simulation/* 接口测试模拟消息堆积处理");
            log.info("如需启用RocketMQ功能，请修改application.yml中的app.mq.enabled为true");
            log.info("==========================================");
        }
    }
}