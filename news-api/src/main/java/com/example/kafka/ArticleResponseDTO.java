package com.example.kafka;

import com.example.domain.Article;
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
