package com.example.repository;

import com.example.domain.Article;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemoryArticleRepository implements ArticleRepository {

    private static Map<Long, Article> store = new HashMap<>();

    private static long sequence = 0L;

    @Override
    public Article save(Article article) {
        article.setId(++sequence);
        store.put(article.getId(), article);
        return article;
    }

    @Override
    public Optional<Article> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Article> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Article> findByTitle(String name) {
        return store.values().stream().filter(article -> article.getTitle().equals(name)).findAny();
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
