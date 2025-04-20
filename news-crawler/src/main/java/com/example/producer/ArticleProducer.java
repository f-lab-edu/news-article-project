package com.example.producer;

import com.example.kafka.ArticleMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleProducer {
    private final KafkaTemplate<String, ArticleMessage> kafkaTemplate;

    public void send(ArticleMessage message) {
        kafkaTemplate.send("news-topic", message);
    }
}
