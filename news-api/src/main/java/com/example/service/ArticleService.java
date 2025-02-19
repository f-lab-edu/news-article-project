package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.mybatis.MyBatisArticleRepository;
import com.example.vo.ArticleSearchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final MyBatisArticleRepository articleRepository;

    @Transactional
    public ArticleFeedbackResponseDTO doFeedback(Long articleId, ArticleFeedbackType type) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사가 존재하지 않습니다."));

        articleRepository.doFeedback(articleId, type);

        return ArticleFeedbackResponseDTO.builder()
                .message("Feedback submitted successfully.")
                .likes(article.getLikes() + (type == ArticleFeedbackType.LIKE ? 1 : 0))
                .dislikes(article.getDislikes() + (type == ArticleFeedbackType.DISLIKE ? 1 : 0))
                .build();
    }

    // 특정 기사id를 가지는 기사의 topic과 같지만 반대 논조의 기사들 return
    public List<Article> getOpposingArticles(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사가 존재하지 않습니다."));
        String topic = article.getTopic();
        ArticleSentiment sentiment = article.getSentiment();

        if (sentiment == ArticleSentiment.NEUTRAL) {
            return Collections.emptyList();
        }

        ArticleSearchVO searchVO = new ArticleSearchVO();
        searchVO.setTopic(topic);

        if (sentiment == ArticleSentiment.NEGATIVE) {
            searchVO.setSentiment(ArticleSentiment.POSITIVE);
        } else {
            searchVO.setSentiment(ArticleSentiment.NEGATIVE);
        }

        return articleRepository.findAll(searchVO);
    }

    // 특정 기사id를 가지는 기사 return
    public Article getSpecificArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사가 존재하지 않습니다."));
    }

    // 검색/필터 + 정렬 + 페이징
    public ArticleResponseDTO searchArticles(ArticleSearchVO searchVO) {
        List<Article> searchResult = articleRepository.findAll(searchVO);

        long totalElements = searchResult.size();
        int totalPages = (int) Math.ceil((double) totalElements / searchVO.getSize());
        List<Article> pageContent = paginate(searchResult, searchVO.getPage(), searchVO.getSize());

        return new ArticleResponseDTO(
                pageContent,
                totalPages,
                totalElements,
                searchVO.getPage()
        );
    }

    private List<Article> paginate(List<Article> list, int page, int size) {
        int startIndex = (page - 1) * size;
        if (startIndex >= list.size()) {
            return Collections.emptyList();
        }
        int endIndex = Math.min(startIndex + size, list.size());
        return list.subList(startIndex, endIndex);
    }
}
