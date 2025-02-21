package com.example.repository.mybatis;

import com.example.domain.ArticleCategory;
import com.example.domain.User;
import com.example.domain.UserSubscription;
import com.example.dto.UserRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    void insertUser(User user);

    User findById(Long userId);

    void updateUser(@Param("userId") Long userId, @Param("userRequestDTO") UserRequestDTO user);

    void deleteUser(Long userId);

    void deleteSubscription(@Param("userId") Long userId, @Param("category") ArticleCategory category, @Param("topic") String topic);

    void insertSubscription(UserSubscription subscription);

    List<UserSubscription> findSubscriptionByUserId(Long userId);

    UserSubscription findOne(@Param("userId") Long userId,
                             @Param("category") ArticleCategory category,
                             @Param("topic") String topic);
}
