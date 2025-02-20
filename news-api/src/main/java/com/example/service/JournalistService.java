package com.example.service;

import com.example.domain.Journalist;
import com.example.dto.JournalistReputationDTO;
import com.example.repository.mybatis.MyBatisJournalistRepository;
import com.example.vo.JournalistLikesDislikesVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JournalistService {
    private final MyBatisJournalistRepository journalistRepository;

    public JournalistReputationDTO getJournalistReputation(Long id) {
        Journalist journalist = journalistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기자가 존재하지 않습니다."));


        JournalistLikesDislikesVO likesDislikes = journalistRepository.getLikesDislikes(id);
        long likes = likesDislikes.getTotalLikes();
        long dislikes = likesDislikes.getTotalDislikes();
        double reputationScore;
        if (likes + dislikes == 0) {
            reputationScore = 0;
        } else {
            reputationScore = (double) (likes - dislikes) / (likes + dislikes) * 100;
        }
        JournalistReputationDTO reputationDTO = new JournalistReputationDTO();
        reputationDTO.setJournalist(journalist.getName());
        reputationDTO.setReputationScore(reputationScore);
        reputationDTO.setDislikes(dislikes);
        reputationDTO.setLikes(likes);

        return reputationDTO;
    }

    @Transactional
    public void updateJournalistReputation(Long id) {
        JournalistLikesDislikesVO likesDislikes = journalistRepository.getLikesDislikes(id);

        long likes = likesDislikes.getTotalLikes();
        long dislikes = likesDislikes.getTotalDislikes();

        double reputationScore = 0;
        if (likes + dislikes > 0) {
            reputationScore = (double) (likes - dislikes) / (likes + dislikes) * 100;
        }
        journalistRepository.updateReputation(id, reputationScore);
    }
}
