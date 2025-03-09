package com.example.controller;

import com.example.config.RedisTestContainerConfig;
import com.example.domain.Article;
import com.example.domain.ArticleFeedbackType;
import com.example.dto.ArticleFeedbackRequestDTO;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.EnrollUserDTO;
import com.example.repository.mybatis.MyBatisArticleRepository;
import com.example.repository.mybatis.MyBatisUserRepository;
import com.example.vo.ArticleSearchVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = RedisTestContainerConfig.class)
@Sql(scripts = "/sql/insert_init_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

class ArticleControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MyBatisArticleRepository articleRepository;
    @Autowired
    private MyBatisUserRepository repository;

    private String jwtToken;
    private Long userId;

    @BeforeEach
    void setup() {
        createAuthHeaders();
    }

    @Test
    void searchArticles() {
        System.out.println(articleRepository.findAll(new ArticleSearchVO()).size());
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
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        long id = articleRepository.findByTitle("인공지능의 위험").get().getId();
        ArticleFeedbackRequestDTO requestDTO = new ArticleFeedbackRequestDTO();
        requestDTO.setType(ArticleFeedbackType.LIKE);

        HttpEntity<ArticleFeedbackRequestDTO> request = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.postForEntity(
                "/articles/" + id + "/feedback",
                request,
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
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ArticleFeedbackRequestDTO requestDTO = new ArticleFeedbackRequestDTO();
        requestDTO.setType(ArticleFeedbackType.LIKE);
        HttpEntity<ArticleFeedbackRequestDTO> request = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.postForEntity(
                "/articles/133/feedback",
                request,
                ArticleFeedbackResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void createAuthHeaders() {
        String url = "/users";
        EnrollUserDTO enrollUserDTO = new EnrollUserDTO();
        enrollUserDTO.setEmail("fasfd@gmail.com");
        enrollUserDTO.setPassword("1234!");
        enrollUserDTO.setUsername("lee");

        HttpEntity<EnrollUserDTO> request = new HttpEntity<>(enrollUserDTO);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, request, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        this.userId = repository.findByEmail(enrollUserDTO.getEmail()).get().getId();
        assertThat(this.userId).isNotNull();

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", enrollUserDTO.getEmail());
        loginRequest.put("password", enrollUserDTO.getPassword());

        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/login", loginRequest, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpHeaders headers = loginResponse.getHeaders();
        assertThat(headers.containsKey(HttpHeaders.SET_COOKIE)).isTrue();

        jwtToken = headers.getFirst(HttpHeaders.SET_COOKIE);
        assertThat(jwtToken).isNotNull();
        System.out.println(jwtToken);
    }


}