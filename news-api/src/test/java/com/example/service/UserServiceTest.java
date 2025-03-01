package com.example.service;

import com.example.domain.ArticleCategory;
import com.example.domain.User;
import com.example.domain.UserSubscription;
import com.example.dto.EnrollUserDTO;
import com.example.dto.UserUpdateDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.repository.mybatis.MyBatisUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    MyBatisUserRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService service;

    @Test
    void signup() {
        EnrollUserDTO dto = new EnrollUserDTO();
        dto.setEmail("test@example.com");
        dto.setUsername("testuser");
        dto.setPassword("password123");

        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);

        when(repository.duplicatedUsername(any())).thenReturn(Optional.empty());
        when(repository.duplicatedEmail(any())).thenReturn(Optional.empty());

        when(passwordEncoder.encode(dto.getPassword())).thenReturn(encodedPassword);

        when(repository.save(any(User.class))).thenReturn(user);

        User result = service.signUp(dto);

        verify(repository).save(any(User.class));
        assertThat(result.getUsername()).isEqualTo(dto.getUsername());
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
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
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("Lee");
        updateDTO.setMailCycle(10);
        updateDTO.setPassword("1234!");

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("OldName");
        mockUser.setMailCycle(5);
        mockUser.setPassword("oldPassword");

        when(repository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(repository.duplicatedUsername(any(User.class))).thenReturn(Optional.empty());

        service.updateUser(userId, updateDTO);

        verify(repository).update(userId, updateDTO);
    }

    @Test
    void bulkAddSubscriptions() {
        Long userId = 1L;
        Map<ArticleCategory, List<String>> subscriptionMap;
        subscriptionMap = new HashMap<>();
        subscriptionMap.put(ArticleCategory.IT, new ArrayList<String>());
        subscriptionMap.get(ArticleCategory.IT).add("AI");
        subscriptionMap.get(ArticleCategory.IT).add("Blockchain");
        subscriptionMap.put(ArticleCategory.SPORTS, new ArrayList<String>());
        subscriptionMap.get(ArticleCategory.SPORTS).add("Soccer");
        when(repository.findSubscriptions(userId)).thenReturn(new ArrayList<UserSubscription>());

        service.bulkAddSubscriptions(userId, subscriptionMap);

        verify(repository).bulkAddSubscriptions(argThat(subscriptions -> {
            assertThat(subscriptions).hasSize(3);

            assertThat(subscriptions).anyMatch(sub ->
                    sub.getUserId().equals(userId) && sub.getCategory() == ArticleCategory.IT && sub.getTopic().equals("AI"));
            assertThat(subscriptions).anyMatch(sub ->
                    sub.getUserId().equals(userId) && sub.getCategory() == ArticleCategory.IT && sub.getTopic().equals("Blockchain"));
            assertThat(subscriptions).anyMatch(sub ->
                    sub.getUserId().equals(userId) && sub.getCategory() == ArticleCategory.SPORTS && sub.getTopic().equals("Soccer"));

            return true;
        }));
    }

    @Test
    void bulkDeleteSubscriptions() {
        Long userId = 1L;
        Map<ArticleCategory, List<String>> subscriptionMap;
        subscriptionMap = new HashMap<>();
        subscriptionMap.put(ArticleCategory.IT, new ArrayList<String>());
        subscriptionMap.get(ArticleCategory.IT).add("AI");
        subscriptionMap.get(ArticleCategory.IT).add("Blockchain");
        subscriptionMap.put(ArticleCategory.SPORTS, new ArrayList<String>());
        subscriptionMap.get(ArticleCategory.SPORTS).add("Soccer");
        List<UserSubscription> existingSubscriptions = new ArrayList<>();
        existingSubscriptions.add(new UserSubscription(userId, ArticleCategory.IT, "AI"));
        existingSubscriptions.add(new UserSubscription(userId, ArticleCategory.IT, "Blockchain"));
        existingSubscriptions.add(new UserSubscription(userId, ArticleCategory.SPORTS, "Soccer"));
        when(repository.findSubscriptions(userId)).thenReturn(existingSubscriptions);

        service.bulkDeleteSubscriptions(userId, subscriptionMap);

        verify(repository).bulkDeleteSubscriptions(argThat(subscriptions ->{
            assertThat(subscriptions).hasSize(3);
            assertThat(subscriptions).containsExactlyInAnyOrderElementsOf(existingSubscriptions);
            return true;
        }));
    }

}
