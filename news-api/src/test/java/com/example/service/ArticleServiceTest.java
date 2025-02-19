package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.repository.mybatis.MyBatisArticleRepository;
import com.example.vo.ArticleSearchVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    MyBatisArticleRepository articleRepository;

    @InjectMocks
    ArticleService articleService;

    Article article1 = new Article();
    Article article2 = new Article();
    Article article3 = new Article();

    @BeforeEach
    void initTest() {
        article1.setId(1L);
        article1.setTitle("김연아 금매달");
        article1.setCategory(ArticleCategory.SPORTS);
        article1.setSentiment(ArticleSentiment.POSITIVE);
        article1.setViews(1500L);
        article1.setJournalistId(1L);
        article1.setTopic("김연아");
        article1.setUpdatedAt(LocalDateTime.of(2010, 12, 15, 8, 0));
        article1.setLikes(1300L);
        article1.setDislikes(0L);

        article2.setId(2L);
        article2.setTitle("손흥민 해트트릭");
        article2.setCategory(ArticleCategory.SPORTS);
        article2.setSentiment(ArticleSentiment.POSITIVE);
        article2.setViews(1300L);
        article2.setJournalistId(2L);
        article2.setTopic("손흥민");
        article2.setUpdatedAt(LocalDateTime.of(2023, 12, 15, 8, 0));
        article2.setLikes(1000L);
        article2.setDislikes(0L);

        article3.setId(3L);
        article3.setTitle("인공지능의 위험");
        article3.setCategory(ArticleCategory.IT);
        article3.setSentiment(ArticleSentiment.NEGATIVE);
        article3.setViews(500L);
        article3.setJournalistId(2L);
        article3.setTopic("인공지능");
        article3.setUpdatedAt(LocalDateTime.of(2024, 12, 15, 8, 0));
        article3.setLikes(500L);
        article3.setDislikes(0L);
    }


    // 기사 좋아요 & 싫어요 테스트
    @Test
    void doFeedback() {
        //given
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article1));

        //when
        ArticleFeedbackResponseDTO response1 = articleService.doFeedback(1L, ArticleFeedbackType.LIKE);
        ArticleFeedbackResponseDTO response2 = articleService.doFeedback(1L, ArticleFeedbackType.DISLIKE);

        //then
        verify(articleRepository).doFeedback(1L, ArticleFeedbackType.LIKE);
        verify(articleRepository).doFeedback(1L, ArticleFeedbackType.DISLIKE);
        assertThat(response1.getMessage()).isEqualTo("Feedback submitted successfully.");
        assertThat(response1.getLikes()).isEqualTo(1301);
        assertThat(response2.getDislikes()).isEqualTo(1);
    }

    // 모든 기사 찾기
    @Test
    void searchAllArticles() {
        //given
        ArticleSearchVO searchVO = new ArticleSearchVO();
        ArrayList<Article> articles = new ArrayList<>();
        articles.add(article1);
        articles.add(article2);
        articles.add(article3);
        when(articleRepository.findAll(eq(searchVO))).thenReturn(articles);

        //when
        ArticleResponseDTO allArticles = articleService.searchArticles(searchVO);

        //then
        verify(articleRepository).findAll(searchVO);
        assertThat(allArticles.getArticles().size()).isEqualTo(3);
    }

    // 기자별 기사 찾기
    @Test
    void searchArticlesByJournalist() {
        //given
        ArrayList<Article> articles1 = new ArrayList<>();
        ArrayList<Article> articles2 = new ArrayList<>();
        ArticleSearchVO requestDTO1 = new ArticleSearchVO();
        ArticleSearchVO requestDTO2 = new ArticleSearchVO();
        requestDTO1.setJournalistId(1L);
        requestDTO2.setJournalistId(2L);
        articles1.add(article1);
        articles2.add(article2);
        articles2.add(article3);

        when(articleRepository.findAll(eq(requestDTO1))).thenReturn(articles1);
        when(articleRepository.findAll(eq(requestDTO2))).thenReturn(articles2);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        verify(articleRepository).findAll(requestDTO1);
        verify(articleRepository).findAll(requestDTO2);
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(1);
        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(2);
    }

    // 카테고리별 기사 찾기
    @Test
    void searchArticlesByCategory() {
        //given
        ArrayList<Article> articles1 = new ArrayList<>();
        ArrayList<Article> articles2 = new ArrayList<>();
        ArticleSearchVO requestDTO1 = new ArticleSearchVO();
        ArticleSearchVO requestDTO2 = new ArticleSearchVO();
        requestDTO1.setJournalistId(1L);
        requestDTO2.setJournalistId(2L);
        articles1.add(article1);
        articles1.add(article2);
        articles2.add(article3);

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);

        when(articleRepository.findAll(requestDTO1)).thenReturn(articles1);
        when(articleRepository.findAll(requestDTO2)).thenReturn(articles2);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        verify(articleRepository).findAll(requestDTO1);
        verify(articleRepository).findAll(requestDTO2);
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(1);
    }

    // 논조 별 기사 탐색
    @Test
    void searchArticlesBySentiment() {
        //given
        ArrayList<Article> articles1 = new ArrayList<>();
        ArrayList<Article> articles2 = new ArrayList<>();
        ArticleSearchVO requestDTO1 = new ArticleSearchVO();
        ArticleSearchVO requestDTO2 = new ArticleSearchVO();
        requestDTO1.setJournalistId(1L);
        requestDTO2.setJournalistId(2L);
        articles1.add(article1);
        articles1.add(article2);
        articles2.add(article3);

        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);

        when(articleRepository.findAll(requestDTO1)).thenReturn(articles1);
        when(articleRepository.findAll(requestDTO2)).thenReturn(articles2);


        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        verify(articleRepository).findAll(requestDTO1);
        verify(articleRepository).findAll(requestDTO2);
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(1);
    }

    // 카테고리와 논조를 모두 필터링한후 기사 검색
    @Test
    void searchArticlesBtCategoryAndSentiment() {
        //given
        ArrayList<Article> articles1 = new ArrayList<>();
        ArrayList<Article> articles2 = new ArrayList<>();

        ArticleSearchVO requestDTO1 = new ArticleSearchVO();
        ArticleSearchVO requestDTO2 = new ArticleSearchVO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);
        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);

        articles1.add(article1);
        articles1.add(article2);

        when(articleRepository.findAll(requestDTO1)).thenReturn(articles1);
        when(articleRepository.findAll(requestDTO2)).thenReturn(articles2);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        verify(articleRepository).findAll(requestDTO1);
        verify(articleRepository).findAll(requestDTO2);
        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }

    // 조회수 기사 정렬
    @Test
    void sortArticlesByViews() {
        //given
        ArrayList<Article> articles1 = new ArrayList<>();
        ArrayList<Article> articles2 = new ArrayList<>();

        ArticleSearchVO requestDTO1 = new ArticleSearchVO();
        ArticleSearchVO requestDTO2 = new ArticleSearchVO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);
        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);
        requestDTO1.setSortField("views");
        requestDTO2.setSortField("views");

        articles1.add(article1);
        articles1.add(article2);

        when(articleRepository.findAll(requestDTO1)).thenReturn(articles1);
        when(articleRepository.findAll(requestDTO2)).thenReturn(articles2);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        verify(articleRepository).findAll(requestDTO1);
        verify(articleRepository).findAll(requestDTO2);

        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }


    // 최신순 기사 정렬
    @Test
    void sortArticlesByDate() {
        //given
        ArrayList<Article> articles1 = new ArrayList<>();
        ArrayList<Article> articles2 = new ArrayList<>();

        ArticleSearchVO requestDTO1 = new ArticleSearchVO();
        ArticleSearchVO requestDTO2 = new ArticleSearchVO();

        requestDTO1.setCategory(ArticleCategory.SPORTS);
        requestDTO2.setCategory(ArticleCategory.IT);
        requestDTO1.setSentiment(ArticleSentiment.POSITIVE);
        requestDTO2.setSentiment(ArticleSentiment.NEUTRAL);
        requestDTO1.setSortField("views");
        requestDTO2.setSortField("views");

        articles1.add(article1);
        articles1.add(article2);

        when(articleRepository.findAll(requestDTO1)).thenReturn(articles1);
        when(articleRepository.findAll(requestDTO2)).thenReturn(articles2);

        //when
        ArticleResponseDTO articleResponseDTO1 = articleService.searchArticles(requestDTO1);
        ArticleResponseDTO articleResponseDTO2 = articleService.searchArticles(requestDTO2);

        //then
        verify(articleRepository).findAll(requestDTO1);
        verify(articleRepository).findAll(requestDTO2);

        assertThat(articleResponseDTO1.getTotalElements()).isEqualTo(2);
        assertThat(articleResponseDTO1.getArticles().get(1).getTitle()).isEqualTo("손흥민 해트트릭");
        assertThat(articleResponseDTO1.getArticles().get(0).getTitle()).isEqualTo("김연아 금매달");

        assertThat(articleResponseDTO2.getTotalElements()).isEqualTo(0);
    }
}