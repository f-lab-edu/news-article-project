package com.example.repository.mybatis;

import com.example.domain.Article;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisArticleRepository implements ArticleRepository {

    private final ArticleMapper articleMapper;

    @Override
    public Optional<Article> findById(Long id) {
        return articleMapper.findById(id);
    }

    @Override
    public Optional<Article> findByTitle(String name) {
        return articleMapper.findByTitle(name);
    }

    @Override
    public List<Article> findAll(ArticleSearchRequestDTO articleSearch) {
        return articleMapper.findAll(articleSearch);
    }

    @Override
    public Article save(Article article) {
        log.info("itemMapper class={}", articleMapper.getClass());
        articleMapper.save(article);
        return article;
    }

    @Override
    public void deleteById(Long id) {
        articleMapper.deleteById(id);
    }

}

