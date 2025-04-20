package com.example.kafka;

import lombok.Data;

@Data
public class JournalistReputationDTO {
    String journalist;
    double reputationScore;
    double likes;
    double dislikes;
}
