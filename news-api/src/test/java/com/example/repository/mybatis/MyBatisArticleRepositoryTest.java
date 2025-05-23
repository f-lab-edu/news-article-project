package com.example.repository.mybatis;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import com.example.vo.ArticleSearchVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@Sql(scripts = "/sql/initialize_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MyBatisArticleRepositoryTest {

    @Autowired
    MyBatisArticleRepository articleRepository;

    Article article1 = new Article();
    Article article2 = new Article();
    Article article3 = new Article();
    Article article4 = new Article();

    @BeforeEach
    void initTest() {
        article1.setTitle("김연아 금매달");
        article1.setCategory(ArticleCategory.SPORTS);
        article1.setSentiment(ArticleSentiment.POSITIVE);
        article1.setViews(1500L);
        article1.setJournalistId(1L);
        article1.setTopic("김연아");
        article1.setUpdatedAt(LocalDateTime.of(2010, 12, 15, 8, 0));
        article1.setLikes(1300L);
        article1.setDislikes(0L);

        article2.setTitle("손흥민 해트트릭");
        article2.setCategory(ArticleCategory.SPORTS);
        article2.setSentiment(ArticleSentiment.POSITIVE);
        article2.setViews(1300L);
        article2.setJournalistId(2L);
        article2.setTopic("손흥민");
        article2.setUpdatedAt(LocalDateTime.of(2023, 12, 15, 8, 0));
        article2.setLikes(1000L);
        article2.setDislikes(0L);

        article3.setTitle("인공지능의 위험");
        article3.setCategory(ArticleCategory.IT);
        article3.setSentiment(ArticleSentiment.NEGATIVE);
        article3.setViews(500L);
        article3.setJournalistId(2L);
        article3.setTopic("인공지능");
        article3.setUpdatedAt(LocalDateTime.of(2024, 12, 15, 8, 0));
        article3.setLikes(500L);
        article3.setDislikes(0L);

        article4.setTitle("인공지능의 영향");
        article4.setCategory(ArticleCategory.IT);
        article4.setSentiment(ArticleSentiment.POSITIVE);
        article4.setViews(500L);
        article4.setJournalistId(3L);
        article4.setTopic("인공지능");
        article4.setUpdatedAt(LocalDateTime.of(2025, 1, 15, 8, 0));
        article4.setLikes(500L);
        article4.setDislikes(0L);

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
        articleRepository.save(article4);
    }

    @Test
    void findById() {
        Article result = articleRepository.findById(article1.getId()).get();
        assertThat(result).isEqualTo(article1);
    }

    @Test
    void findByTitle() {
        Article result = articleRepository.findByTitle("김연아 금매달").get();
        assertThat(result).isEqualTo(article1);
    }

    @Test
    void findArticles() {
        // 전체
        test(null, null, null, null, article1, article2, article3, article4);
        // 카테고리 검색
        test(ArticleCategory.IT, null, null, null, article3, article4);
        // 감정 분석
        test(null, null, ArticleSentiment.POSITIVE, null, article1, article2, article4);
        // 기자별 검색
        test(null, 1L, null, null, article1);
        // 카테고리+감정
        test(ArticleCategory.IT, null, ArticleSentiment.POSITIVE, null, article4);
        // 감정+토픽
        test(null, null, ArticleSentiment.POSITIVE, "인공지능", article4);
    }

    @Test
    void save() {
        List<Article> all = articleRepository.findAll(null);
        assertThat(all.size()).isEqualTo(4);
    }

    @Test
    void deleteById() {
        List<Article> all = articleRepository.findAll(null);
        int originalSize = all.size();
        articleRepository.deleteById(article1.getId());
        all = articleRepository.findAll(null);
        int nextSize = all.size();
        assertThat(originalSize).isEqualTo(nextSize + 1);
    }

    void test(ArticleCategory category, Long journalistId, ArticleSentiment sentiment, String topic, Article... articles) {
        ArticleSearchVO searchVO = new ArticleSearchVO();
        searchVO.setSentiment(sentiment);
        searchVO.setCategory(category);
        searchVO.setJournalistId(journalistId);
        searchVO.setTopic(topic);

        List<Article> result = articleRepository.findAll(searchVO);
        assertThat(result).containsExactly(articles);
    }
}
