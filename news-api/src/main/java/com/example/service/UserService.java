package com.example.service;

import com.example.domain.ArticleCategory;
import com.example.domain.User;
import com.example.domain.UserSubscription;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.repository.mybatis.MyBatisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MyBatisUserRepository userRepository;

    public User signUp(User user) {
        userRepository.save(user);
        return user;
    }

    public UserSubscriptionInfoDTO getSubscriptionInfoOfUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UserSubscription> subs = userRepository.findSubscriptions(userId);
        Integer mailCycle = user.getMailCycle();

        UserSubscriptionInfoDTO subscriptionInfoDTO = new UserSubscriptionInfoDTO();
        subscriptionInfoDTO.setSubs(subs);
        subscriptionInfoDTO.setMailCycle(mailCycle);

        return subscriptionInfoDTO;
    }

    public void updateUser(Long id, UserRequestDTO update) {
        userRepository.update(id, update);
    }

    public void addSubscription(Long userId, ArticleCategory category, String topic) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        boolean alreadySubscribe = userRepository.findOneSubscription(userId, category, topic).isPresent();

        if (alreadySubscribe) {
            return;
        }

        UserSubscription subscription = new UserSubscription();
        subscription.setUserId(userId);
        subscription.setCategory(category);
        subscription.setTopic(topic);
        userRepository.insertSubscription(subscription);
    }

    public void deleteSubscription(Long userId, ArticleCategory category, String topic) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        userRepository.deleteSubscription(userId, category, topic);
    }
}
