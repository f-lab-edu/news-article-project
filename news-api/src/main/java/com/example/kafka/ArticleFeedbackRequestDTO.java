package com.example.kafka;

import com.example.domain.ArticleFeedbackType;
import lombok.Data;

@Data
public class ArticleFeedbackRequestDTO {
    private ArticleFeedbackType type;
}
