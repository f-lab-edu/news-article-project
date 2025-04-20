package com.example.util;

import com.example.domain.ArticleCategory;

public class CategoryMapper {
    public static ArticleCategory mapSid1ToCategory(String sid1) {
        switch(sid1) {
            case "100" :
                return ArticleCategory.POLITICS;
            case "101":
                return ArticleCategory.ECONOMICS;
            case "102":
                return ArticleCategory.SOCIETY;
            case "103":
                return ArticleCategory.LIFE;
            case "104":
                return ArticleCategory.WORLD;
            case "105":
                return ArticleCategory.IT;
            case "106":
                return ArticleCategory.SPORTS;
            case "230":
                return ArticleCategory.SCIENCE;
            default:
                return ArticleCategory.WORLD;
        }
    }
}
