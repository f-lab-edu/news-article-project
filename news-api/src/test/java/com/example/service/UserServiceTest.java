package com.example.service;

import com.example.domain.ArticleCategory;
import com.example.domain.User;
import com.example.domain.UserSubscription;
import com.example.dto.UserRequestDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.repository.mybatis.MyBatisUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    MyBatisUserRepository repository;

    @InjectMocks
    UserService service;

    @Test
    void signup() {
        User user = new User();
        when(repository.save(user)).thenReturn(user);

        User result = service.signUp(user);

        verify(repository).save(user);
        assertThat(user).isEqualTo(user);
    }

    @Test
    void getSubscriptionInfoOfUser() {
        User user = new User();
        user.setId(1L);
        user.setMailCycle(7);

        Map<ArticleCategory, List<String>> subscription = new HashMap<>();
        subscription.put(ArticleCategory.IT, new ArrayList<>());
        subscription.get(ArticleCategory.IT).add("AI");
        subscription.get(ArticleCategory.IT).add("Crypto");
        user.setSubscription(subscription);

        List<UserSubscription> subscriptions = new ArrayList<>();
        UserSubscription subscription1 = new UserSubscription();
        subscription1.setUserId(1L);
        subscription1.setId(1L);
        subscription1.setCategory(ArticleCategory.IT);
        subscription1.setTopic("AI");
        UserSubscription subscription2 = new UserSubscription();
        subscription2.setUserId(1L);
        subscription2.setId(2L);
        subscription2.setCategory(ArticleCategory.IT);
        subscription2.setTopic("Crypto");
        subscriptions.add(subscription1);
        subscriptions.add(subscription2);

        UserSubscriptionInfoDTO userSubscriptionInfoDTO = new UserSubscriptionInfoDTO();
        userSubscriptionInfoDTO.setMailCycle(7);
        userSubscriptionInfoDTO.setSubs(subscriptions);

        when(repository.findSubscriptions(1L)).thenReturn(subscriptions);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserSubscriptionInfoDTO subscriptionInfoOfUser = service.getSubscriptionInfoOfUser(1L);

        assertThat(subscriptionInfoOfUser).isEqualTo(userSubscriptionInfoDTO);
        verify(repository).findSubscriptions(1L);
        verify(repository).findById(1L);
    }

    @Test
    void updateUser() {
        Long userId = 1L;
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setUsername("Lee");
        updateDTO.setMail_cycle(10);
        updateDTO.setPassword("1234!");

        service.updateUser(userId, updateDTO);

        verify(repository).update(userId, updateDTO);
    }

    @Test
    void addSubscription_UserNotFound() {
        Long userId = 999L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addSubscription(userId, ArticleCategory.IT, "AI"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found: 999");
    }

    @Test
    void addSubscription_AlreadySubscribed() {
        Long userId = 1L;

        User user = new User();
        user.setId(1L);
        user.setEmail("afas@gmail.com");
        user.setUsername("test");
        user.setPassword("test");
        Map<ArticleCategory, List<String>> subscribe = new HashMap<>();
        subscribe.put(ArticleCategory.IT, new ArrayList<>());
        subscribe.get(ArticleCategory.IT).add("AI");
        user.setSubscription(subscribe);

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.findOneSubscription(1L, ArticleCategory.IT, "AI"))
                .thenReturn(Optional.of(new UserSubscription()));

        service.addSubscription(userId, ArticleCategory.IT, "AI");

        verify(repository, never()).insertSubscription(any());
        verify(repository, never()).findSubscriptions(userId);
    }

    @Test
    void addSubscription_Normal() {
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.of(new User()));
        when(repository.findOneSubscription(userId, ArticleCategory.IT, "AI")).thenReturn(Optional.empty());

        service.addSubscription(userId, ArticleCategory.IT, "AI");

        verify(repository).insertSubscription(any(UserSubscription.class));
        verify(repository).findOneSubscription(userId, ArticleCategory.IT, "AI");
        verify(repository).findById(userId);
    }

    @Test
    void deleteSubscription_UserNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteSubscription(2L, ArticleCategory.IT, "AI"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found: 2");
    }

    @Test
    void deleteSubscription_Normal() {
        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.of(new User()));

        service.deleteSubscription(userId, ArticleCategory.IT, "AI");

        verify(repository).deleteSubscription(userId, ArticleCategory.IT, "AI");

    }

}
