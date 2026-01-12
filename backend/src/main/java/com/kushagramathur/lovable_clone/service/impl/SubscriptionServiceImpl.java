package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.subscription.SubscriptionResponse;
import com.kushagramathur.lovable_clone.entity.Plan;
import com.kushagramathur.lovable_clone.entity.Subscription;
import com.kushagramathur.lovable_clone.entity.User;
import com.kushagramathur.lovable_clone.enums.SubscriptionStatus;
import com.kushagramathur.lovable_clone.error.ResourceNotFoundException;
import com.kushagramathur.lovable_clone.mapper.SubscriptionMapper;
import com.kushagramathur.lovable_clone.repository.PlanRepository;
import com.kushagramathur.lovable_clone.repository.ProjectMemberRepository;
import com.kushagramathur.lovable_clone.repository.SubscriptionRepository;
import com.kushagramathur.lovable_clone.repository.UserRepository;
import com.kushagramathur.lovable_clone.security.AuthUtil;
import com.kushagramathur.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final  SubscriptionMapper subscriptionMapper;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final Integer FREE_TIER_PROJECT_LIMIT = 1;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();
        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.TRIALING
        )).orElse(
                new Subscription()
        );

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        boolean subscriptionHasBeenUpdated = false;

        if (status != null && status != subscription.getStatus()) {
            subscription.setStatus(status);
            subscriptionHasBeenUpdated = true;
        }

        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
            subscription.setCurrentPeriodStart(periodStart);
            subscriptionHasBeenUpdated = true;
        }

        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            subscriptionHasBeenUpdated = true;
        }

        if (cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            subscriptionHasBeenUpdated = true;
        }

        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            Plan plan = getPlan(planId);
            subscription.setPlan(plan);
            subscriptionHasBeenUpdated = true;
        }

        if (subscriptionHasBeenUpdated) {
            log.debug("Updating subscription {} with new values", gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodEnd(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            log.debug("Subscription {} is already marked as PAST_DUE", gatewaySubscriptionId);
            return;
        }

        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);
    }

    @Override
    public boolean canCreateNewProject() {
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(authUtil.getCurrentUserId());

        if (currentSubscription.plan() == null) {
            return countOfOwnedProjects < FREE_TIER_PROJECT_LIMIT;
        }

        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
    }

    // UTILITY METHODS

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId.toString()));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan",  planId.toString()));
    }
}
