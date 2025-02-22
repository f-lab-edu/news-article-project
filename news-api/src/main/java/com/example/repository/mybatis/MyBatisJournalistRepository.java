package com.example.repository.mybatis;

import com.example.domain.Journalist;
import com.example.vo.JournalistLikesDislikesVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MyBatisJournalistRepository {
    private final JournalistMapper journalistMapper;

    public Journalist save(Journalist journalist) {
        log.info("itemMapper class={}", journalistMapper.getClass());
        journalistMapper.save(journalist);
        return journalist;
    }

    public Optional<Journalist> findById(Long id) {
        return journalistMapper.findById(id);
    }

    public void deleteById(Long id) {
        journalistMapper.deleteById(id);
    }

    public List<Long> findAllJournalistIds() {
        return journalistMapper.findAllJournalistIds();
    }

    public JournalistLikesDislikesVO getLikesDislikes(Long journalistId) {
        return journalistMapper.getLikesDislikes(journalistId);
    }

    public void updateReputation(Long journalistId, double reputationScore) {
        journalistMapper.updateReputation(journalistId, reputationScore);
    }
}
