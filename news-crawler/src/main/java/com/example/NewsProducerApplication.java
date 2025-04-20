package com.example;

import com.example.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@RequiredArgsConstructor
@SpringBootApplication
public class NewsProducerApplication {

    private final NewsCrawlerService crawlerService;

    public static void main(String[] args) {
        SpringApplication.run(NewsProducerApplication.class, args);
    }

}
