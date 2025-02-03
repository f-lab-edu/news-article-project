package com.example.domain;

import lombok.Data;

@Data
public class Journalist {

    private Article[] reportedNews;
    private String name;
    private Double reputationScore;
}
