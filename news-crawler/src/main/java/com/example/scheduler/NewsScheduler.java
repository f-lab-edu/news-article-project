package com.example.scheduler;

import com.example.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsScheduler implements CommandLineRunner {

    private final NewsCrawlerService crawlerService;

    @Override
    public void run(String... args) throws Exception {
        crawlPeriodically();
    }

    @Scheduled(fixedRate = 60000)
    public void crawlPeriodically() {
        List<String> urls = List.of(
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=100", // POLITICS
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=101", // ECONOMICS
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=102", // SOCIETY
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=103", // LIFE
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=104", // WORLD
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=105", // IT
                "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=106" // SPORTS
//              "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=230"  // SCIENCE
        );
        for (String url : urls) {
            crawlerService.crawlAndProcess(url);
        }
    }
}
