package com.example.controller;

import com.example.domain.ArticleCategory;
import com.example.dto.EnrollUserDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.dto.UserSubscriptionRequestDTO;
import com.example.dto.UserUpdateDTO;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping
    public void enrollUser(@RequestBody @Valid EnrollUserDTO enrollUserDTO) {
        userService.signUp(enrollUserDTO);
    }

    @PutMapping("/{userId}")
    public void updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        userService.updateUser(userId, userUpdateDTO);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PutMapping("/{userId}/subscription")
    public void userSubscription(@PathVariable Long userId, @RequestBody UserSubscriptionRequestDTO dto) {
        Map<ArticleCategory, List<String>> subscription = dto.getSubscription();

        for (Map.Entry<ArticleCategory, List<String>> entry : subscription.entrySet()) {
            ArticleCategory category = entry.getKey();
            List<String> topics = entry.getValue();
            for (String topic : topics) {
                userService.addSubscription(userId, category, topic);
            }
        }
    }

    @GetMapping("/{userId}/subscription")
    public UserSubscriptionInfoDTO getUserSubscriptionInfo(@PathVariable Long userId) {
        return userService.getSubscriptionInfoOfUser(userId);
    }

    @DeleteMapping("/{userId}/subscription")
    public void deleteUserSubscription(@PathVariable Long userId, @RequestBody UserSubscriptionRequestDTO dto) {
        Map<ArticleCategory, List<String>> subscription = dto.getSubscription();

        for (Map.Entry<ArticleCategory, List<String>> entry : subscription.entrySet()) {
            ArticleCategory category = entry.getKey();
            List<String> topics = entry.getValue();
            for (String topic : topics) {
                userService.deleteSubscription(userId, category, topic);
            }
        }
    }
}
