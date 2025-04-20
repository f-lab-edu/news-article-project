package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private ArticleCategory category;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime updatedAt;

    private String topic;

    @Enumerated(EnumType.STRING)
    private ArticleSentiment sentiment;

    private Long views = 0L;
    private Long likes = 0L;
    private Long dislikes = 0L;

    @ManyToOne
    @JoinColumn(name = "journalist_id")
    private Journalist journalist;
}
