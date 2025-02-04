package com.example.repository;

import com.example.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemoryArticleRepositoryTest {

    MemoryArticleRepository repository = new MemoryArticleRepository();

    @AfterEach
    void clear() {
        repository.clear();
    }

    @Test
    void save() {
        Article article = new Article();
        article.setTitle("1");

        repository.save(article);

        Article result = repository.findById(article.getId()).get();
        assertThat(result).isEqualTo(article);
    }

    @Test
    void findById() {
        Article article1 = new Article();
        article1.setTitle("1");
        Article article2 = new Article();
        article2.setTitle("2");
        repository.save(article1);
        repository.save(article2);

        Article result = repository.findById(article1.getId()).get();

        assertThat(result).isEqualTo(article1);
    }

    @Test
    void findAll() {
        Article article1 = new Article();
        article1.setTitle("1");
        Article article2 = new Article();
        article2.setTitle("2");
        repository.save(article1);
        repository.save(article2);

        List<Article> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findByTitle() {
        Article article1 = new Article();
        article1.setTitle("1");
        Article article2 = new Article();
        article2.setTitle("2");
        repository.save(article1);
        repository.save(article2);

        Optional<Article> result = repository.findByTitle("1");

        assertThat(result.get().getTitle()).isEqualTo("1");
    }

    @Test
    void deleteById() {
        Article article1 = new Article();
        article1.setTitle("1");
        Article article2 = new Article();
        article2.setTitle("2");
        repository.save(article1);
        repository.save(article2);

        repository.deleteById(article1.getId());
        List<Article> all = repository.findAll();

        assertThat(all.size()).isEqualTo(1);
    }
}