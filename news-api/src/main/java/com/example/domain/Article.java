package com.example.domain;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Article {
    private Long id;
    private String title;
    private ArticleCategory category;
    private ArticleSentiment sentiment;
    private String content;
    private Long views;
    private Long journalistId;
    private String topic;
    private LocalDateTime updatedAt;
    private Long likes;
    private Long dislikes;
}
