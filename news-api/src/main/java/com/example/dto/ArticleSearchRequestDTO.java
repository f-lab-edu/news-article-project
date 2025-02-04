package com.example.dto;

import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleSearchRequestDTO {

    private ArticleCategory category;
    private ArticleSentiment sentiment;
    private Long journalistId;

    private int page = 1;
    private int size = 10;

    private String sortField = "id";
    private boolean descending = true;
}
