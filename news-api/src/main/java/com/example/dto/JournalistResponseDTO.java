package com.example.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class JournalistResponseDTO {
    private final String journalist;
    private final double reputationScore;
    private final long likes;
    private final long dislikes;
}
