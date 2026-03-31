package com.kushagramathur.distributed_lovable_clone.account_service.controller;

import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.AuthResponse;
import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.LoginRequest;
import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.SignupRequest;
import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.UserProfileResponse;
import com.kushagramathur.distributed_lovable_clone.account_service.service.AuthService;
//import com.kushagramathur.distributed_lovable_clone.account_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
//    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

//    @GetMapping("/me")
//    public ResponseEntity<UserProfileResponse> getProfile() {
//        Long userId = 1L;
//        return ResponseEntity.ok(userService.getprofile(userId));
//    }
}
