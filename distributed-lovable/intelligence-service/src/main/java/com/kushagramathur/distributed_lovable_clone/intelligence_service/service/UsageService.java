package com.kushagramathur.distributed_lovable_clone.intelligence_service.service;

public interface UsageService {
    void recordTokenUsage(Long userId, int actualTokens);

    void checkDailyTokensUsage();
}
