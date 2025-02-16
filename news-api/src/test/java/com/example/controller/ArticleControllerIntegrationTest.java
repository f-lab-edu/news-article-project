package com.example.controller;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackRequestDTO;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.memory.MemoryArticleRepository;
import com.example.repository.mybatis.MyBatisArticleRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/insert_init_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

class ArticleControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MyBatisArticleRepository articleRepository;


    @Test
    void searchArticles() {
        System.out.println(articleRepository.findAll(new ArticleSearchRequestDTO()).size());
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
        long id = articleRepository.findByTitle("김연아 금매달").get().getId();
        ResponseEntity<Article> response = restTemplate.getForEntity("/articles/" + id, Article.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Article article = response.getBody();
        assertThat(article).isNotNull();
        assertThat(article.getTitle()).isEqualTo("김연아 금매달");
    }

    @Test
    void getOpposingArticles() {
        long id = articleRepository.findByTitle("인공지능의 위험").get().getId();
        ResponseEntity<List<Article>> response = restTemplate.exchange(
                "/articles/" + id + "/opposing",
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
        long id = articleRepository.findByTitle("인공지능의 위험").get().getId();
        ArticleFeedbackRequestDTO requestDTO = new ArticleFeedbackRequestDTO();
        requestDTO.setType(ArticleFeedbackType.LIKE);

        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.postForEntity(
                "/articles/" + id + "/feedback",
                requestDTO,
                ArticleFeedbackResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArticleFeedbackResponseDTO body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("Feedback submitted successfully.");
        assertThat(body.getLikes()).isEqualTo(501);
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

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}