package com.example.controller;

import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import com.example.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public ArticleResponseDTO searchArticles(@RequestBody ArticleSearchRequestDTO requestDTO) {
        return articleService.searchArticles(requestDTO);
    }
}
