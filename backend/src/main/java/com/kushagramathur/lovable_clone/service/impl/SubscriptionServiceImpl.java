package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.subscription.CheckoutRequest;
import com.kushagramathur.lovable_clone.dto.subscription.CheckoutResponse;
import com.kushagramathur.lovable_clone.dto.subscription.PortalResponse;
import com.kushagramathur.lovable_clone.dto.subscription.SubscriptionResponse;
import com.kushagramathur.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
