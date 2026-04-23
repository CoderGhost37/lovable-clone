package com.kushagramathur.distributed_lovable_clone.workspace_service.client;

import com.kushagramathur.distributed_lovable_clone.common_lib.dto.PlanDto;
import com.kushagramathur.distributed_lovable_clone.common_lib.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@FeignClient(name = "account-service", path = "/account", url="${ACCOUNT_SERVICE_URI:}")
public interface AccountClient {

    @GetMapping("/internal/v1/user/by-email")
    Optional<UserDto> getUserByEmail(String email);

    @GetMapping("/internal/v1/billing/current-plan")
    PlanDto getCurrentSubscribedPlan();
}
