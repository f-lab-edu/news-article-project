package com.example.service;

import com.example.domain.Article;
import com.example.domain.ArticleCategory;
import com.example.kafka.ArticleMessage;
import com.example.producer.ArticleProducer;
import com.example.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final ArticleRepository articleRepository;
    private final ArticleProducer articleProducer;

    public void processArticle(ArticleMessage message) {
        if (!articleRepository.existsByTitle(message.getTitle())) {
            Article article = new Article();
            article.setTitle(message.getTitle());
            article.setContent(message.getContent());
            article.setUpdatedAt(message.getUpdatedAt());
            article.setCategory(ArticleCategory.valueOf(message.getCategory()));
            article.setViews(0L);
            article.setLikes(0L);
            article.setDislikes(0L);
            article.setJournalist(null);

            articleRepository.save(article);
            articleProducer.send(message);
        }
    }
}
