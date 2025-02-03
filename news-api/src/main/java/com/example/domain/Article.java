package com.example.domain;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Article {
    private Long id;
    private String title;
    private ArticleCategory category;
    private ArticleSentiment sentiment;
    private Long views;
    private LocalDateTime dateTime;
    private Long likes;
    private Long dislikes;
}
