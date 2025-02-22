package com.example.dto;

import com.example.domain.ArticleCategory;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserSubscriptionRequestDTO {
    private Map<ArticleCategory, List<String>> subscription;
}
