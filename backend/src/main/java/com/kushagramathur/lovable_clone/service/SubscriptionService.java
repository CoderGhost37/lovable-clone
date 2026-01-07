package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.subscription.CheckoutRequest;
import com.kushagramathur.lovable_clone.dto.subscription.CheckoutResponse;
import com.kushagramathur.lovable_clone.dto.subscription.PortalResponse;
import com.kushagramathur.lovable_clone.dto.subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);
}
