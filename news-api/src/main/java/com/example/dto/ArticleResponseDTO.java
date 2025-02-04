package com.example.dto;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ArticleResponseDTO {

    private final List<Article> articles;
    private final int totalPages;
    private final long totalElements;
    private final int currentPage;
}
