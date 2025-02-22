package com.example.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class User {
    private Long id;

    private String email;
    private String username;
    private String password;
    // 만약 ArticleCategory만 있고 String이 없으면 그 분야의 기사 구독으로 간주
    // 아닐경우 그 분야의 특정 주제 기사 구독으로 간주
    private Map<ArticleCategory, List<String>> subscription;
    private Integer mailCycle;
}
