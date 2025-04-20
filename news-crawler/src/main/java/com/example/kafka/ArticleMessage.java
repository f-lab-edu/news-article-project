package com.example.kafka;

import com.example.domain.ArticleCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleMessage {
    private String title;
    private String category;
    private String content;
    private LocalDateTime updatedAt;
}
