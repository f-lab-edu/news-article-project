package com.example.service;

import com.example.domain.ArticleCategory;
import com.example.kafka.ArticleMessage;
import com.example.util.CategoryMapper;
import com.example.util.Sid1Extractor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsCrawlerService {

    private final NewsService newsService;

    public void crawlAndProcess(String categoryUrl) {
        List<ArticleMessage> results = crawlNaverNews(categoryUrl);
        for (ArticleMessage msg : results) {
            newsService.processArticle(msg);
        }
    }

    public List<ArticleMessage> crawlNaverNews(String categoryUrl) {
        List<ArticleMessage> results = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(categoryUrl).get();
            Elements headlines = doc.select("a[href^=https://n.news.naver.com]");

            int count = 0;
            for (Element element : headlines) {
                if (count++ >= 5) break;

                String title = element.text();
                String link = element.attr("href");

                Document detail = Jsoup.connect(link).get();
                String content = detail.select(".go_trans._article_content").text();

                String dateTimeStr = detail.select(".media_end_head_info_datestamp_time").attr("data-date-time");
                LocalDateTime updatedAt = null;
                try {
                    updatedAt = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    updatedAt = LocalDateTime.now();
                }

                String sid1 = Sid1Extractor.extractSid1(categoryUrl);
                var category = CategoryMapper.mapSid1ToCategory(sid1);

                ArticleMessage message = new ArticleMessage();
                message.setTitle(title);
                message.setContent(content);
                message.setUpdatedAt(updatedAt);
                message.setCategory(category.name());

                results.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
