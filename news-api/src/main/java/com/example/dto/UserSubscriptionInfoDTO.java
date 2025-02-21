package com.example.dto;

import com.example.domain.UserSubscription;
import lombok.Data;

import java.util.List;

@Data
public class UserSubscriptionInfoDTO {
    private List<UserSubscription> subs;
    private Integer mailCycle;
}
