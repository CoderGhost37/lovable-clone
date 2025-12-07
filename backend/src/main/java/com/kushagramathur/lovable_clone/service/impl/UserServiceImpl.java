package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.auth.UserProfileResponse;
import com.kushagramathur.lovable_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public UserProfileResponse getprofile(Long userId) {
        return null;
    }
}
