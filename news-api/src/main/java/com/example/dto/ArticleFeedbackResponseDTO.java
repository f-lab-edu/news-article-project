package com.example.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleFeedbackResponseDTO {
    String message;
    Long likes;
    Long dislikes;
}
