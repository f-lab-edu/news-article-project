package com.example.vo;

import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleSearchVO {
    private ArticleCategory category;
    private ArticleSentiment sentiment;
    private Long journalistId;
    private String topic;

    private int page = 1;
    private int size = 10;

    private String sortField = "id";
}
