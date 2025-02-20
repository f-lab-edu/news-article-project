package com.example.dto;

import lombok.Data;

@Data
public class JournalistReputationDTO {
    String journalist;
    double reputationScore;
    double likes;
    double dislikes;
}
