package com.example.domain;

import lombok.Data;

@Data
public class UserSubscription {
    private Long id;
    private Long userId;
    private ArticleCategory category;
    private String topic;

    public UserSubscription(Long userId, ArticleCategory category, String topic) {
        this.userId = userId;
        this.category = category;
        this.topic = topic;
    }

    public UserSubscription() {
    }
}
