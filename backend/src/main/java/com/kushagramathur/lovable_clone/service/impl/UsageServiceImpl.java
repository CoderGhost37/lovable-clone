package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.kushagramathur.lovable_clone.dto.subscription.UsageTodayResponse;
import com.kushagramathur.lovable_clone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
