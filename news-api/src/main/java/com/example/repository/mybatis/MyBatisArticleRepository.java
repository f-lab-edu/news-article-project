package com.example.repository.mybatis;

import com.example.domain.Article;
import com.example.domain.ArticleFeedbackType;
import com.example.vo.ArticleSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MyBatisArticleRepository {


    private final ArticleMapper articleMapper;

    public Optional<Article> findById(Long id) {
        return articleMapper.findById(id);
    }

    public Optional<Article> findByTitle(String name) {
        return articleMapper.findByTitle(name);
    }

    public List<Article> findAll(ArticleSearchVO articleSearch) {
        return articleMapper.findAll(articleSearch);
    }

    public Article save(Article article) {
        log.info("itemMapper class={}", articleMapper.getClass());
        articleMapper.save(article);
        return article;
    }

    public void deleteById(Long id) {
        articleMapper.deleteById(id);
    }

    public void doFeedback(Long articleId, ArticleFeedbackType articleFeedbackType) {
        if (articleFeedbackType == ArticleFeedbackType.LIKE) {
            articleMapper.updateLikes(articleId);
        } else {
            articleMapper.updateDislikes(articleId);
        }
    }

}

