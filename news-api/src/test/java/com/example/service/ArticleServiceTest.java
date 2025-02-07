package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackRequestDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import com.example.repository.MemoryArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ArticleServiceTest {

    MemoryArticleRepository articleRepository;
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

        Article article3 = new Article();
        article3.setTitle("인공지능의 위험");
        article3.setCategory(ArticleCategory.IT);
        article3.setSentiment(ArticleSentiment.NEGATIVE);
        article3.setViews(500L);
        article3.setJournalistId(2L);
        article3.setTopic("인공지능");
        article3.setUpdatedAt(LocalDateTime.of(2024, 12, 15, 8, 0));
        article3.setLikes(500L);
        article3.setDislikes(0L);

        articleRepository = new MemoryArticleRepository();
        articleService = new ArticleService(articleRepository);

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(article3);
    }

    @AfterEach
    void clearRepository() {
        articleRepository.clear();
        articleRepository.resetSequence();
    }

    // 기사 좋아요 & 싫어요 테스트
    @Test
    void doFeedback() {
        ArticleFeedbackType type1 = ArticleFeedbackType.LIKE;
        ArticleFeedbackType type2 = ArticleFeedbackType.DISLIKE;
        Long like1 = articleRepository.findById(1L).get().getLikes();
        Long dislike2 = articleRepository.findById(2L).get().getDislikes();

        articleService.doFeedback(1L, type1);
        articleService.doFeedback(2L, type2);

        Article first = articleRepository.findById(1L).get();
        Article second = articleRepository.findById(2L).get();
        assertThat(first.getLikes() == like1 + 1);
        assertThat(second.getDislikes() == dislike2 + 1);
    }

    // 카테고리 & 논조 없이 기사 찾기
    @Test
    void searchAllArticles() {
        //given
        ArticleSearchRequestDTO requestDTO = new ArticleSearchRequestDTO();

        //when
        ArticleResponseDTO articleResponseDTO = articleService.searchArticles(requestDTO);

        //then
        assertThat(articleResponseDTO.getTotalElements()).isEqualTo(3);
        assertThat(articleResponseDTO.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");
        assertThat(articleResponseDTO.getArticles().get(2).getTitle()).isEqualTo("인공지능의 위험");
    }

    // 기자별 기사 찾기
    @Test
    void searchArticlesByJournalist() {
        //given
        ArticleSearchRequestDTO requestDTO1 = new ArticleSearchRequestDTO();
        ArticleSearchRequestDTO requestDTO2 = new ArticleSearchRequestDTO();

        requestDTO1.setJournalistId(1L);
        requestDTO2.setJournalistId(2L);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(1);
        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(2);
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

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(1);
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