package com.example.domain;

import lombok.Data;

@Data
public class UserSubscription {
    private Long id;
    private Long userId;
    private ArticleCategory category;
    private String topic;
}
