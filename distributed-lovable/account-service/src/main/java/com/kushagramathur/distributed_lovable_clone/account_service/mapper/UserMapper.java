package com.kushagramathur.distributed_lovable_clone.account_service.mapper;

import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.SignupRequest;
import com.kushagramathur.distributed_lovable_clone.account_service.dto.auth.UserProfileResponse;
import com.kushagramathur.distributed_lovable_clone.account_service.entity.User;
import com.kushagramathur.distributed_lovable_clone.common_lib.dto.UserDto;
import com.kushagramathur.distributed_lovable_clone.common_lib.security.JwtUserPrinciple;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignupRequest signupRequest);

    @Mapping(source = "userId", target = "id")
    UserProfileResponse toUserProfileResponse(JwtUserPrinciple user);

    UserDto toUserDto(User user);
}
