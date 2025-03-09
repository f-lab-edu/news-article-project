package com.example.service;

import com.example.Exception.DuplicatedEmail;
import com.example.Exception.DuplicatedUsername;
import com.example.domain.ArticleCategory;
import com.example.domain.User;
import com.example.domain.UserSubscription;
import com.example.dto.EnrollUserDTO;
import com.example.dto.UserUpdateDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.repository.mybatis.MyBatisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MyBatisUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;

    // 유저의 이메일과 유저이름은 unique 해야함
    // 비밀번호 암호화 추가
    public User signUp(EnrollUserDTO enrollUserDTO) {
        User user = new User();
        user.setEmail(enrollUserDTO.getEmail());
        user.setUsername(enrollUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(enrollUserDTO.getPassword()));
        if (userRepository.duplicatedUsername(user).orElse(null) != null) {
            throw new DuplicatedUsername("Duplicated username");
        }
        if (userRepository.duplicatedEmail(user).orElse(null) != null) {
            throw new DuplicatedEmail("Duplicated email");
        }
        userRepository.save(user);
        return user;
    }

    public void updateUser(Long id, UserUpdateDTO update) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(update.getUsername());
        user.setPassword(passwordEncoder.encode(update.getPassword()));
        user.setMailCycle(update.getMailCycle());
        if (userRepository.duplicatedUsername(user).orElse(null) != null) {
            throw new DuplicatedUsername("Duplicated username");
        }
        userRepository.update(id, update);
    }

    @Cacheable(value="userSubscription", key="'users:subscriptionInfo:'+#userId",cacheManager = "cacheManager")
    public UserSubscriptionInfoDTO getSubscriptionInfoOfUser(Long userId) {
        System.out.println("🟢 DB에서 구독 정보를 가져옵니다 (캐시 적용 전)");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UserSubscription> subs = userRepository.findSubscriptions(userId);
        Integer mailCycle = user.getMailCycle();

        UserSubscriptionInfoDTO subscriptionInfoDTO = new UserSubscriptionInfoDTO();
        subscriptionInfoDTO.setSubs(subs);
        subscriptionInfoDTO.setMailCycle(mailCycle);

        System.out.println("🟢 Redis에 저장될 데이터: " + subscriptionInfoDTO);

        // ✅ Redis에 직접 저장
        String cacheKey = "users:subscriptionInfo:" + userId;
        redisTemplate.opsForValue().set(cacheKey, subscriptionInfoDTO);

        return subscriptionInfoDTO;
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        userRepository.delete(userId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found by email: " + email));
    }

    @Transactional
    @CacheEvict(value="userSubscription", key="'users:subscriptionInfo:'+#userId",cacheManager = "cacheManager")
    public void bulkAddSubscriptions(Long userId, Map<ArticleCategory,List<String>> subscriptionMap) {
        List<UserSubscription> existingSubscriptions = userRepository.findSubscriptions(userId);
        Set<String> existingTopics = existingSubscriptions.stream()
                .map(UserSubscription -> UserSubscription.getTopic())
                .collect(Collectors.toSet());

        List<UserSubscription> subscriptions = new ArrayList<>();
        for (Map.Entry<ArticleCategory, List<String>> entry : subscriptionMap.entrySet()) {
            for (String topic : entry.getValue()) {
                if (!existingTopics.contains(topic)) {
                    subscriptions.add(new UserSubscription(userId, entry.getKey(), topic));
                }
            }
        }
        userRepository.bulkAddSubscriptions(subscriptions);
    }

    @Transactional
    @CacheEvict(value="userSubscription", key="'users:subscriptionInfo:'+#userId",cacheManager = "cacheManager")
    public void bulkDeleteSubscriptions(Long userId, Map<ArticleCategory,List<String>> subscriptionMap) {
        List<UserSubscription> existingSubscriptions = userRepository.findSubscriptions(userId);
        Set<String> existingTopics = existingSubscriptions.stream()
                .map(UserSubscription -> UserSubscription.getTopic())
                .collect(Collectors.toSet());

        List<UserSubscription> subscriptionsToDelete = new ArrayList<>();
        for (Map.Entry<ArticleCategory, List<String>> entry : subscriptionMap.entrySet()) {
            for (String topic : entry.getValue()) {
                if (existingTopics.contains(topic)) {
                    subscriptionsToDelete.add(new UserSubscription(userId, entry.getKey(), topic));
                } else {
                    throw new IllegalArgumentException("해당 구독이 존재하지 않습니다: " + topic);
                }
            }
        }
        userRepository.bulkDeleteSubscriptions(subscriptionsToDelete);
    }
}
