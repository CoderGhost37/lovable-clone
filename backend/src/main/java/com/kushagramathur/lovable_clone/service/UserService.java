package com.kushagramathur.lovable_clone.service;

import com.kushagramathur.lovable_clone.dto.auth.UserProfileResponse;

public interface UserService {
    UserProfileResponse getprofile(Long userId);
}
