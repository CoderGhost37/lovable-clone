package com.kushagramathur.lovable_clone.mapper;

import com.kushagramathur.lovable_clone.dto.auth.SignupRequest;
import com.kushagramathur.lovable_clone.dto.auth.UserProfileResponse;
import com.kushagramathur.lovable_clone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);
}
