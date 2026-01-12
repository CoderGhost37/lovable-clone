package com.kushagramathur.lovable_clone.mapper;

import com.kushagramathur.lovable_clone.dto.subscription.PlanResponse;
import com.kushagramathur.lovable_clone.dto.subscription.SubscriptionResponse;
import com.kushagramathur.lovable_clone.entity.Plan;
import com.kushagramathur.lovable_clone.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);

}
