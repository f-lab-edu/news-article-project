package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import com.example.repository.mybatis.MyBatisArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

        ArticleSearchRequestDTO requestDTO = new ArticleSearchRequestDTO();
        requestDTO.setTopic(topic);

        if (sentiment == ArticleSentiment.NEGATIVE) {
            requestDTO.setSentiment(ArticleSentiment.POSITIVE);
        } else {
            requestDTO.setSentiment(ArticleSentiment.NEGATIVE);
        }

        return articleRepository.findAll(requestDTO);
    }

    // 특정 기사id를 가지는 기사 return
    public Article getSpecificArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사가 존재하지 않습니다."));
    }

    // 검색/필터 + 정렬 + 페이징
    public ArticleResponseDTO searchArticles(ArticleSearchRequestDTO requestDTO) {
        List<Article> searchResult = articleRepository.findAll(requestDTO);

        searchResult = sortArticles(searchResult, requestDTO.getSortField(), requestDTO.isDescending());


        long totalElements = searchResult.size();
        int totalPages = (int) Math.ceil((double) totalElements / requestDTO.getSize());
        List<Article> pageContent = paginate(searchResult, requestDTO.getPage(), requestDTO.getSize());

        return new ArticleResponseDTO(
                pageContent,
                totalPages,
                totalElements,
                requestDTO.getPage()
        );
    }

    private List<Article> sortArticles(List<Article> list, String sortField, boolean descending) {
        Comparator<Article> comparator = null;
        switch(sortField) {
            case "views":
                comparator = (o1, o2) -> {
                    if (o1.getViews() < o2.getViews()) {
                        return -1;
                    } else if (o1.getViews() > o2.getViews()) {
                        return 1;
                    }
                    return 0;
                };
                break;
            case "date":
                comparator = (o1, o2) -> o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
                break;
            case "id":
                comparator = (o1, o2) -> {
                    if (o1.getId() < o2.getId()) {
                        return 1;
                    } else if (o1.getId() > o2.getId()) {
                        return -1;
                    }
                    return 0;
                };
                break;
            default:
                break;
        }
        if (descending) {
            comparator = comparator.reversed();
        }
        list.sort(comparator);
        return list;
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
