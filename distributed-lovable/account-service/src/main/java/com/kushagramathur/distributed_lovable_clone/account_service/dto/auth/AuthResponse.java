package com.kushagramathur.distributed_lovable_clone.account_service.dto.auth;

public record AuthResponse(String token, UserProfileResponse user) {
}
