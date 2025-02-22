package com.example.repository.mybatis;

import com.example.domain.ArticleCategory;
import com.example.domain.User;
import com.example.domain.UserSubscription;
import com.example.dto.UserUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyBatisUserRepository {
    private final UserMapper userMapper;

    public User save(User user) {
        userMapper.insertUser(user);
        return user;
    }

    public Long duplicatedUsername(User user) {
        return userMapper.duplicatedUsername(user);
    }

    public Long duplicatedEmail(User user) {
        return userMapper.duplicatedEmail(user);
    }

    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(userMapper.findById(userId));
    }

    public void update(Long userId, UserUpdateDTO update) {
        userMapper.updateUser(userId, update);
    }

    public void delete(Long userId) {
        userMapper.deleteUser(userId);
    }

    public List<UserSubscription> findSubscriptions(Long userId) {
        return userMapper.findSubscriptionByUserId(userId);
    }

    public void insertSubscription(UserSubscription subscription) {
        userMapper.insertSubscription(subscription);
    }

    public void deleteSubscription(Long userId, ArticleCategory category, String topic) {
        userMapper.deleteSubscription(userId, category, topic);
    }

    public Optional<UserSubscription> findOneSubscription(Long userId, ArticleCategory category, String topic) {
        return Optional.ofNullable(userMapper.findOne(userId, category, topic));
    }
}
