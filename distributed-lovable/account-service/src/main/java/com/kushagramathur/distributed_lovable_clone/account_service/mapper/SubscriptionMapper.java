package com.kushagramathur.distributed_lovable_clone.account_service.mapper;

import com.kushagramathur.distributed_lovable_clone.account_service.dto.subscription.SubscriptionResponse;
import com.kushagramathur.distributed_lovable_clone.account_service.entity.Plan;
import com.kushagramathur.distributed_lovable_clone.account_service.entity.Subscription;
import com.kushagramathur.distributed_lovable_clone.common_lib.dto.PlanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanDto toPlanResponse(Plan plan);

}
