package com.example.repository.mybatis;

import com.example.domain.Article;
import com.example.domain.Journalist;
import com.example.vo.JournalistLikesDislikesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface JournalistMapper {
    void save(Journalist journalist);

    Optional<Journalist> findById(Long id);

    void deleteById(Long id);

    List<Long> findAllJournalistIds();

    JournalistLikesDislikesVO getLikesDislikes(@Param("journalistId") Long journalistId);

    void updateReputation(@Param("journalistId") Long journalistId, @Param("reputationScore") double reputationScore);
}
