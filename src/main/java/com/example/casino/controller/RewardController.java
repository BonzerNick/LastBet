package com.example.casino.controller;

import com.example.casino.config.CustomUserDetails;
import com.example.casino.model.User;
import com.example.casino.service.RewardService;
import com.example.casino.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Autowired
    private UserService userService;

    @GetMapping("/daily")
    public String getDailyRewardStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        return rewardService.claimDailyReward(user);
    }

    @GetMapping("/streak")
    public String getStreakRewardStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        return rewardService.claimStreakReward(user);
    }
}
