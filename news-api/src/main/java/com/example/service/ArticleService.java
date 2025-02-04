package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.domain.ArticleSentiment;
import com.example.dto.ArticleResponseDTO;
import com.example.dto.ArticleSearchRequestDTO;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

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
