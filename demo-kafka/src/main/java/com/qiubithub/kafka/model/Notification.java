package com.qiubithub.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String notificationId;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private String type;
    private boolean read;
}