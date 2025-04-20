package com.example.controller;

import com.example.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsCrawlerService crawlerService;

    @GetMapping("/crawl")
    public String crawl(@RequestParam(defaultValue = "https://news.naver.com/main/list.naver?mode=LPOD&mid=sec&oid=001&listType=title") String url) {
        crawlerService.crawlAndProcess(url);
        return "크롤링 완료";
    }
}
