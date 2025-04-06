package com.example.controller;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/insert_init_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/init_user_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)


class ArticleControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MyBatisArticleRepository articleRepository;
    @Autowired
    private MyBatisUserRepository userRepository;

    private String jwtToken;
    private Long userId;


    @BeforeEach
    void setup() {
        // 회원가입
        String email = "testuser1@example.com";
        String password = "1234!";
        String username = "user";

        EnrollUserDTO enrollUserDTO = new EnrollUserDTO();
        enrollUserDTO.setEmail(email);
        enrollUserDTO.setPassword(password);
        enrollUserDTO.setUsername(username);

        HttpEntity<EnrollUserDTO> enrollRequest = new HttpEntity<>(enrollUserDTO);
        ResponseEntity<Void> enrollResponse = restTemplate.postForEntity("/users", enrollRequest, null);

        assertThat(enrollResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        this.userId = userRepository.findByEmail(email).get().getId();
        assertThat(this.userId).isNotNull();

        // 로그인
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/login", loginRequest, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpHeaders headers = loginResponse.getHeaders();
        assertThat(headers.containsKey(HttpHeaders.SET_COOKIE)).isTrue();

        jwtToken = headers.getFirst(HttpHeaders.SET_COOKIE);
        System.out.println("JWT 쿠키: " + jwtToken);
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
        long id = articleRepository.findByTitle("인공지능의 위험").get().getId();

        ArticleFeedbackRequestDTO requestDTO = new ArticleFeedbackRequestDTO();
        requestDTO.setType(ArticleFeedbackType.LIKE);

        // 🔥 jwt=... 만 추출해서 쿠키로 사용해야 Tomcat이 인식함
        String actualJwt = jwtToken.split(";")[0];

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, actualJwt); // 여기서 전체 쿠키 문자열이 아닌, jwt=...만 전달
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ArticleFeedbackRequestDTO> entity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.exchange(
                "/articles/" + id + "/feedback",
                HttpMethod.POST,
                entity,
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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "testToken");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ArticleFeedbackResponseDTO> response = restTemplate.exchange(
                "/articles/133/feedback",
                HttpMethod.POST,
                entity,
                ArticleFeedbackResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}