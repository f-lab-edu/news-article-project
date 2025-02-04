package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import com.example.repository.MemoryArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ArticleServiceTest {

    ArticleRepository articleRepository;
    ArticleService articleService;

    @BeforeEach
    void initTest() {
        Article article1 = new Article();
        article1.setTitle("김연아 금매달");
        article1.setCategory(ArticleCategory.SPORTS);
        article1.setSentiment(ArticleSentiment.POSITIVE);
        article1.setViews(1500L);
        article1.setJournalistId(1L);
        article1.setTopic("김연아");
        article1.setUpdatedAt(LocalDateTime.of(2010, 12, 15, 8, 0));
        article1.setLikes(1300L);
        article1.setDislikes(0L);

        Article article2 = new Article();
        article2.setTitle("손흥민 해트트릭");
        article2.setCategory(ArticleCategory.SPORTS);
        article2.setSentiment(ArticleSentiment.POSITIVE);
        article2.setViews(1300L);
        article2.setJournalistId(2L);
        article2.setTopic("손흥민");
        article2.setUpdatedAt(LocalDateTime.of(2023, 12, 15, 8, 0));
        article2.setLikes(1000L);
        article2.setDislikes(0L);

        articleRepository = new MemoryArticleRepository();
        articleService = new ArticleService(articleRepository);

        articleRepository.save(article1);
        articleRepository.save(article2);
    }

    // 카테고리 & 논조 없이 기사 찾기
    @Test
    void searchArticles() {
        //given
        ArticleSearchRequestDTO requestDTO = new ArticleSearchRequestDTO();

        //when
        ArticleResponseDTO articleResponseDTO = articleService.searchArticles(requestDTO);

        //then
        assertThat(articleResponseDTO.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");
    }


    // 카테고리별 기사 찾기
    @Test
    void searchArticlesByCategory() {
        //given
        ArticleSearchRequestDTO requestDTO1 = new ArticleSearchRequestDTO();
        ArticleSearchRequestDTO requestDTO2 = new ArticleSearchRequestDTO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }

    // 논조 별 기사 탐색
    @Test
    void searchArticlesBySentiment() {
        //given
        ArticleSearchRequestDTO requestDTO1 = new ArticleSearchRequestDTO();
        ArticleSearchRequestDTO requestDTO2 = new ArticleSearchRequestDTO();

        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }

    // 카테고리와 논조를 모두 필터링한후 기사 검색
    @Test
    void searchArticlesBtCategoryAndSentiment() {
        //given
        ArticleSearchRequestDTO requestDTO1 = new ArticleSearchRequestDTO();
        ArticleSearchRequestDTO requestDTO2 = new ArticleSearchRequestDTO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);
        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }

    // 조회수 기사 정렬
    @Test
    void sortArticlesByViews() {
        //given
        ArticleSearchRequestDTO requestDTO1 = new ArticleSearchRequestDTO();
        ArticleSearchRequestDTO requestDTO2 = new ArticleSearchRequestDTO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);
        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);
        requestDTO1.setSortField("views");
        requestDTO2.setSortField("views");

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }


    // 최신순 기사 정렬
    @Test
    void sortArticlesByDate() {
        //given
        ArticleSearchRequestDTO requestDTO1 = new ArticleSearchRequestDTO();
        ArticleSearchRequestDTO requestDTO2 = new ArticleSearchRequestDTO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);
        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);
        requestDTO1.setSortField("views");
        requestDTO2.setSortField("views");

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }
}