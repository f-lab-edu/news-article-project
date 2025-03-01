package com.example.controller;

import com.example.dto.EnrollUserDTO;
import com.example.dto.UserSubscriptionInfoDTO;
import com.example.dto.UserSubscriptionRequestDTO;
import com.example.dto.UserUpdateDTO;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping
    public void enrollUser(@RequestBody @Valid EnrollUserDTO enrollUserDTO) {
        userService.signUp(enrollUserDTO);
    }

    // 로그인한 사용자만 접근 가능, 자기자신의 것만 수정 가능
    @PreAuthorize("isAuthenticated() and #userId == authentication.details")
    @PutMapping("/{userId}")
    public void updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        userService.updateUser(userId, userUpdateDTO);
    }

    @PreAuthorize("isAuthenticated() and #userId == authentication.details")
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PreAuthorize("isAuthenticated() and #userId == authentication.details")
    @PutMapping("/{userId}/subscription")
    public void userSubscription(@PathVariable Long userId, @RequestBody UserSubscriptionRequestDTO dto) {
        userService.bulkAddSubscriptions(userId, dto.getSubscription());
    }

    @PreAuthorize("isAuthenticated() and #userId == authentication.details")
    @GetMapping("/{userId}/subscription")
    public UserSubscriptionInfoDTO getUserSubscriptionInfo(@PathVariable Long userId) {
        return userService.getSubscriptionInfoOfUser(userId);
    }

    @PreAuthorize("isAuthenticated() and #userId == authentication.details")
    @DeleteMapping("/{userId}/subscription")
    public void deleteUserSubscription(@PathVariable Long userId, @RequestBody UserSubscriptionRequestDTO dto) {
        userService.bulkDeleteSubscriptions(userId, dto.getSubscription());
    }
}
