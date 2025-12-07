package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.subscription.PlanResponse;
import com.kushagramathur.lovable_clone.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
