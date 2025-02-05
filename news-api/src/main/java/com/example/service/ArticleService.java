package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleFeedbackType;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleFeedbackResponseDTO;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleFeedbackResponseDTO doFeedback(Long articleId, ArticleFeedbackType type) {
        Optional<Article> target = articleRepository.findById(articleId);
        if (target.isEmpty()) {
            return ArticleFeedbackResponseDTO.builder().message("Feedback failed").build();
        }
        Article targetArticle = target.get();
        if (type == ArticleFeedbackType.DISLIKE) {
            targetArticle.setDislikes(targetArticle.getDislikes() + 1);
        } else {
            targetArticle.setLikes(targetArticle.getLikes() + 1);
        }
        return ArticleFeedbackResponseDTO.builder()
                .message("Feedback submitted successfully.")
                .likes(targetArticle.getLikes())
                .dislikes(targetArticle.getDislikes())
                .build();
    }

    // 특정 기사id를 가지는 기사의 topic과 같지만 반대 논조의 기사들 return
    public List<Article> getOpposingArticles(Long articleId) {
        List<Article> all = articleRepository.findAll();
        Article article = articleRepository.findById(articleId).get();
        String topic = article.getTopic();
        ArticleSentiment sentiment = article.getSentiment();

        if (sentiment == ArticleSentiment.NEUTRAL) {
            return Collections.emptyList();
        }

        List<Article> collect = all.stream().filter(a -> Objects.equals(a.getTopic(), topic))
                .collect(Collectors.toList());

        if (sentiment == ArticleSentiment.NEGATIVE) {
            return collect.stream().filter(a -> a.getSentiment() == ArticleSentiment.POSITIVE)
                    .collect(Collectors.toList());
        }

        return collect.stream().filter(a -> a.getSentiment() == ArticleSentiment.NEGATIVE)
                .collect(Collectors.toList());
    }

    // 특정 기사id를 가지는 기사 return
    public Article getSpecificArticle(Long articleId) {
        return articleRepository.findById(articleId).get();
    }

    // 검색/필터 + 정렬 + 페이징
    public ArticleResponseDTO searchArticles(ArticleSearchRequestDTO requestDTO) {
        List<Article> all = articleRepository.findAll();

        all = filterArticles(all, requestDTO.getCategory(), requestDTO.getSentiment(), requestDTO.getJournalistId());

        all = sortArticles(all, requestDTO.getSortField(), requestDTO.isDescending());


        long totalElements = all.size();
        int totalPages = (int) Math.ceil((double) totalElements / requestDTO.getSize());
        List<Article> pageContent = paginate(all, requestDTO.getPage(), requestDTO.getSize());

        return new ArticleResponseDTO(
                pageContent,
                totalPages,
                totalElements,
                requestDTO.getPage()
        );
    }

    private List<Article> filterArticles(
            List<Article> list,
            ArticleCategory category,
            ArticleSentiment sentiment,
            Long journalistId
    ) {
        if (category != null) {
            list = list.stream()
                    .filter(a -> a.getCategory() == category)
                    .collect(Collectors.toList());
        }
        if (sentiment != null) {
            list = list.stream()
                    .filter(a -> a.getSentiment() == sentiment)
                    .collect(Collectors.toList());
        }
        if (journalistId != null) {
            list = list.stream()
                    .filter(a -> Objects.equals(a.getJournalistId(), journalistId))
                    .collect(Collectors.toList());
        }
        return list;
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
