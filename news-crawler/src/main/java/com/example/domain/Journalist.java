package com.example.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Journalist {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Double reputationScore;

    @OneToMany(mappedBy = "journalist", cascade = CascadeType.ALL)
    private List<Article> reportedNews;
}
