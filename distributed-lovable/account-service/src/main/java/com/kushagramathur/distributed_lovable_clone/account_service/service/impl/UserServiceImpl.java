package com.kushagramathur.distributed_lovable_clone.account_service.service.impl;

import com.kushagramathur.distributed_lovable_clone.account_service.entity.User;
import com.kushagramathur.distributed_lovable_clone.account_service.repository.UserRepository;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.JwtUserPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new JwtUserPrinciple(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
