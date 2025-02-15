package com.example.repository.mybatis;

import com.example.domain.Article;
import com.example.dto.ArticleSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArticleMapper {
    Optional<Article> findById(Long id);

    Optional<Article> findByTitle(String name);

    List<Article> findAll(ArticleSearchRequestDTO articleSearch);

    void save(Article article);

    void deleteById(Long id);

    void clear();
}
