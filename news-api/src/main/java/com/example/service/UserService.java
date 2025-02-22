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
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MyBatisUserRepository userRepository;

    // 유저의 이메일과 유저이름은 unique 해야함
    public User signUp(EnrollUserDTO enrollUserDTO) {
        User user = new User();
        user.setEmail(enrollUserDTO.getEmail());
        user.setUsername(enrollUserDTO.getUsername());
        user.setPassword(enrollUserDTO.getPassword());
        if (userRepository.duplicatedUsername(user) != null) {
            throw new DuplicatedUsername("Duplicated username");
        }
        if (userRepository.duplicatedEmail(user) != null) {
            throw new DuplicatedEmail("Duplicated email");
        }
        userRepository.save(user);
        return user;
    }

    public void updateUser(Long id, UserUpdateDTO update) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(update.getUsername());
        user.setPassword(update.getPassword());
        user.setMailCycle(update.getMailCycle());
        if (userRepository.duplicatedUsername(user) != null) {
            throw new DuplicatedUsername("Duplicated username");
        }
        userRepository.update(id, update);
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

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        userRepository.delete(userId);
    }
}
