package com.example.controller;

import com.example.config.RedisTestContainerConfig;
import com.example.dto.JournalistReputationDTO;
import com.example.repository.mybatis.MyBatisJournalistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = RedisTestContainerConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JournalistControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MyBatisJournalistRepository repository;

    @Test
    void getJournalistReputationScore() {
        String url = "/journalist/1/reputation";

        ResponseEntity<JournalistReputationDTO> response = restTemplate.getForEntity(url, JournalistReputationDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JournalistReputationDTO reputationDTO = response.getBody();
        assertThat(reputationDTO).isNotNull();
        assertThat(reputationDTO.getLikes()).isEqualTo(1800);
        assertThat(reputationDTO.getDislikes()).isEqualTo(0);
        assertThat(reputationDTO.getReputationScore()).isEqualTo((double) (1800 - 0) / (1800 + 0) * 100);
    }

    @Test
    void updateJournalistReputationScore() {
        String url = "/journalist/1/update-reputation";

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo("Reputation updated");
    }
}