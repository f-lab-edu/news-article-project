package com.example.dto;

import com.example.domain.ArticleFeedbackType;
import lombok.Data;

@Data
public class ArticleFeedbackRequestDTO {
    private ArticleFeedbackType type;
}
