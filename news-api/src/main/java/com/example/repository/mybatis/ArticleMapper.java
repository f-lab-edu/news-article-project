package com.example.repository.mybatis;

import com.example.domain.Article;
import com.example.vo.ArticleSearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArticleMapper {
    Optional<Article> findById(Long id);

    Optional<Article> findByTitle(String name);

    List<Article> findAll(ArticleSearchVO articleSearch);

    void save(Article article);

    void deleteById(Long id);

    void updateLikes(Long id);

    void updateDislikes(Long id);

}
