package com.kushagramathur.lovable_clone.service;

public interface UsageService {
    void recordTokenUsage(int actualTokens);

    void checkDailyTokensUsage();
}
