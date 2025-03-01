package com.example.controller;

import com.example.domain.ArticleCategory;
import com.example.dto.EnrollUserDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.dto.UserSubscriptionRequestDTO;
import com.example.dto.UserUpdateDTO;
import com.example.repository.mybatis.MyBatisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Sql(scripts = "/sql/init_user_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MyBatisUserRepository repository;

    private String jwtToken;
    private Long userId;

    @BeforeEach
    void setup() {
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
        System.out.println(jwtToken);
    }


    @Test
    void enrollUserDuplicatedUserName() {
        String url = "/users";
        EnrollUserDTO enrollUserDTO = new EnrollUserDTO();
        enrollUserDTO.setEmail("tesdda@gmail.com");
        enrollUserDTO.setPassword("1234!");
        enrollUserDTO.setUsername("tester");

        HttpEntity<EnrollUserDTO> request = new HttpEntity<>(enrollUserDTO);
        ResponseEntity<Object> response = restTemplate.postForEntity(url, request, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void enrollUserDuplicatedEmail() {
        String url = "/users";
        EnrollUserDTO enrollUserDTO = new EnrollUserDTO();
        enrollUserDTO.setEmail("test@gmail.com");
        enrollUserDTO.setPassword("1234!");
        enrollUserDTO.setUsername("lee");

        HttpEntity<EnrollUserDTO> request = new HttpEntity<>(enrollUserDTO);
        ResponseEntity<Object> response = restTemplate.postForEntity(url, request, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testEnrollUser_InvalidEmail() {
        EnrollUserDTO enrollUserDTO = new EnrollUserDTO();
        enrollUserDTO.setEmail("invalidEmail"); // Invalid email
        enrollUserDTO.setUsername("testUser");
        enrollUserDTO.setPassword("securePassword");

        HttpEntity<EnrollUserDTO> request = new HttpEntity<>(enrollUserDTO);
        ResponseEntity<Void> response = restTemplate.postForEntity("/users", request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void updateUser() {
        String url = "/users";
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("modify");
        updateDTO.setPassword("test");
        updateDTO.setMailCycle(7);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserUpdateDTO> request = new HttpEntity<>(updateDTO,headers);
        ResponseEntity<Void> response = restTemplate.exchange(url + "/" + userId, HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteUser() {
        String url = "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);

        HttpEntity<UserUpdateDTO> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url + "/" + userId, HttpMethod.DELETE, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void addUserSubscription() {
        String url = "/users";
        UserSubscriptionRequestDTO requestDTO = new UserSubscriptionRequestDTO();
        HashMap<ArticleCategory, List<String>> map = new HashMap<>();
        map.put(ArticleCategory.IT, new ArrayList<>());
        map.get(ArticleCategory.IT).add("AI");
        requestDTO.setSubscription(map);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserSubscriptionRequestDTO> request = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<Void> response = restTemplate.exchange(url + "/" + userId + "/" + "subscription", HttpMethod.PUT, request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserSubscriptionInfo() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserSubscriptionInfoDTO> response = restTemplate.exchange("/users" + "/" + userId + "/subscription", HttpMethod.GET, request, UserSubscriptionInfoDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deleteUserSubscription() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        UserSubscriptionRequestDTO subscriptionRequestDTO = new UserSubscriptionRequestDTO();
        Map<ArticleCategory, List<String>> map = new HashMap<>();
        map.put(ArticleCategory.IT, new ArrayList<>());
        map.get(ArticleCategory.IT).add("AI");
        map.get(ArticleCategory.IT).add("IOT");
        String url = "/users";
        subscriptionRequestDTO.setSubscription(map);

        HttpEntity<UserSubscriptionRequestDTO> request = new HttpEntity<>(subscriptionRequestDTO, headers);
        restTemplate.exchange(url + "/" + userId + "/" + "subscription", HttpMethod.PUT, request, Void.class);

        map.get(ArticleCategory.IT).removeLast();
        subscriptionRequestDTO.setSubscription(map);
        ResponseEntity<Void> response = restTemplate.exchange(url + "/" + userId + "/" + "subscription", HttpMethod.DELETE, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}