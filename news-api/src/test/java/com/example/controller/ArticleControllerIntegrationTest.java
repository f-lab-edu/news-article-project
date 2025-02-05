package com.example.controller;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackRequestDTO;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.MemoryArticleRepository;
import com.example.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MemoryArticleRepository articleRepository;


    @BeforeEach
    public void setUp() {
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

        Article article4 = new Article();
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

    @AfterEach
    public void clear() {
        articleRepository.clear();
        articleRepository.resetSequence();
    }

    @Test
    void searchArticles() {
        String url = "/articles";

        ResponseEntity<ArticleResponseDTO> response = restTemplate.getForEntity(url, ArticleResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ArticleResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getArticles().size()).isEqualTo(4);

        url = "/articles?category=SPORTS&sentiment=POSITIVE";
        response = restTemplate.getForEntity(url, ArticleResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getArticles().size()).isEqualTo(2);
    }

    @Test
    void getSpecificArticle() {
        ResponseEntity<Article> response = restTemplate.getForEntity("/articles/1", Article.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Article article = response.getBody();
        assertThat(article).isNotNull();
        assertThat(article.getTitle()).isEqualTo("김연아 금매달");
    }

    @Test
    void getOpposingArticles() {
        ResponseEntity<List<Article>> response = restTemplate.exchange(
                "/articles/3/opposing",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Article>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Article> opposingList = response.getBody();

        assertThat(opposingList).isNotNull();
        assertThat(opposingList.get(0).getTitle()).isEqualTo("인공지능의 영향");
    }

    @Test
    void feedbackArticle_success() {
        ArticleFeedbackRequestDTO requestDTO = new ArticleFeedbackRequestDTO();
        requestDTO.setType(ArticleFeedbackType.LIKE);

        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.postForEntity(
                "/articles/1/feedback",
                requestDTO,
                ArticleFeedbackResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArticleFeedbackResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("Feedback submitted successfully.");
        assertThat(body.getLikes()).isEqualTo(1301);
        assertThat(body.getDislikes()).isEqualTo(0);
    }

    @Test
    void testFeedbackArticle_failed() {
        ArticleFeedbackRequestDTO requestDTO = new ArticleFeedbackRequestDTO();
        requestDTO.setType(ArticleFeedbackType.LIKE);

        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.postForEntity(
                "/articles/133/feedback",
                requestDTO,
                ArticleFeedbackResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ArticleFeedbackResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("Feedback failed");
    }

}