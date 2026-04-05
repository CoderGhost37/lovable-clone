package com.kushagramathur.distributed_lovable_clone.account_service.service.impl;

import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.AuthResponse;
import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.LoginRequest;
import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.SignupRequest;
import com.kushagramathur.distributed_lovable_clone.account_service.entity.User;
import com.kushagramathur.distributed_lovable_clone.common_lib.error.BadRequestException;
import com.kushagramathur.distributed_lovable_clone.account_service.mapper.UserMapper;
import com.kushagramathur.distributed_lovable_clone.account_service.repository.UserRepository;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.AuthUtil;
import com.kushagramathur.distributed_lovable_clone.account_service.service.AuthService;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.JwtUserPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BadRequestException("User already exists with username "+request.username());
        });

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        JwtUserPrinciple jwtUserPrinciple = new JwtUserPrinciple(
                user.getId(),
                user.getName(),
                user.getUsername(),
                null,
                new ArrayList<>()
        );

        String token = authUtil.generateAccessToken(jwtUserPrinciple);

        return new AuthResponse(token, userMapper.toUserProfileResponse(jwtUserPrinciple));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        JwtUserPrinciple user = (JwtUserPrinciple) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }
}
