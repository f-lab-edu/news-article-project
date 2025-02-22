package com.example.service;

import com.example.domain.*;
import com.example.dto.JournalistReputationDTO;
import com.example.repository.mybatis.MyBatisJournalistRepository;
import com.example.vo.JournalistLikesDislikesVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalistServiceTest {

    @Mock
    MyBatisJournalistRepository repository;

    @InjectMocks
    JournalistService service;

    @Test
    void getJournalistReputationNormalCase() {
        //정상 흐름
        //given
        Long journalistId = 1L;

        Journalist journalist = new Journalist();
        journalist.setId(journalistId);
        journalist.setName("홍길동");

        JournalistLikesDislikesVO likesDislikesVO = new JournalistLikesDislikesVO();
        likesDislikesVO.setTotalLikes(10L);
        likesDislikesVO.setTotalDislikes(5L);

        when(repository.findById(journalistId)).thenReturn(Optional.of(journalist));
        when(repository.getLikesDislikes(journalistId)).thenReturn(likesDislikesVO);

        //when
        JournalistReputationDTO reputationDTO = service.getJournalistReputation(journalistId);

        //then
        assertNotNull(reputationDTO);
        assertEquals("홍길동", reputationDTO.getJournalist());
        assertEquals(10L, reputationDTO.getLikes());
        assertEquals(5L, reputationDTO.getDislikes());
        assertEquals(5.0 / 15 * 100, reputationDTO.getReputationScore());

        verify(repository).findById(journalistId);
        verify(repository).getLikesDislikes(journalistId);
    }

    @Test
    void getJournalistReputationAbnormalCase() {
        //예외 흐름
        //given
        Long journalistId = 99L;
        when(repository.findById(journalistId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> service.getJournalistReputation(journalistId));

        //verify
        verify(repository, times(1)).findById(journalistId);
        verify(repository, never()).getLikesDislikes(journalistId);
    }

    @Test
    void updateJournalistReputation() {
        // given
        Long journalistId = 2L;

        JournalistLikesDislikesVO likesDislikesVO = new JournalistLikesDislikesVO();
        likesDislikesVO.setTotalLikes(20L);
        likesDislikesVO.setTotalDislikes(10L);

        when(repository.getLikesDislikes(journalistId)).thenReturn(likesDislikesVO);

        // when
        service.updateJournalistReputation(journalistId);

        // then
        double expectedReputation = (double) (20 - 10) / (20 + 10) * 100;
        verify(repository).updateReputation(journalistId,expectedReputation);
    }
}