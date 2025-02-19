package com.example.controller;

import com.example.domain.Article;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleFeedbackRequestDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.service.ArticleService;
import com.example.vo.ArticleSearchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ArticleResponseDTO searchArticles(@ModelAttribute ArticleSearchRequestDTO requestDTO) {
        ArticleSearchVO searchVO = new ArticleSearchVO();
        searchVO.setSentiment(requestDTO.getSentiment());
        searchVO.setTopic(requestDTO.getTopic());
        searchVO.setJournalistId(requestDTO.getJournalistId());
        searchVO.setCategory(requestDTO.getCategory());
        searchVO.setPage(requestDTO.getPage());
        searchVO.setSize(requestDTO.getSize());
        searchVO.setSortField(requestDTO.getSortField());
        return articleService.searchArticles(searchVO);
    }

    @GetMapping("/{articleId}")
    public Article getSpecificArticle(@PathVariable Long articleId) {
        return articleService.getSpecificArticle(articleId);
    }

    @GetMapping("/{articleId}/opposing")
    public List<Article> getOpposingArticles(@PathVariable Long articleId) {
        return articleService.getOpposingArticles(articleId);
    }

    @PostMapping("/{articleId}/feedback")
    public ResponseEntity<ArticleFeedbackResponseDTO> feedbackArticle(@PathVariable Long articleId, @RequestBody ArticleFeedbackRequestDTO type) {

        ArticleFeedbackResponseDTO result = articleService.doFeedback(articleId, type.getType());

        if (result.getMessage().equals("Feedback failed")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }
}
