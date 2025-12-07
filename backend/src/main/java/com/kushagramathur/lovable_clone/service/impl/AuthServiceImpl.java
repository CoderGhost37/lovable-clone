package com.kushagramathur.lovable_clone.service.impl;

import com.kushagramathur.lovable_clone.dto.auth.AuthResponse;
import com.kushagramathur.lovable_clone.dto.auth.LoginRequest;
import com.kushagramathur.lovable_clone.dto.auth.SignupRequest;
import com.kushagramathur.lovable_clone.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse signup(SignupRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
