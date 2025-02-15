package com.example.repository;


import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleSearchRequestDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

    Optional<Article> findById(Long id);

    Optional<Article> findByTitle(String name);

    List<Article> findAll(ArticleSearchRequestDTO articleSearch);

    Article save(Article article);

    void deleteById(Long id);
}
