package com.monexus.finance.user.mapper;

import com.monexus.finance.user.dto.request.RegisterRequest;
import com.monexus.finance.user.dto.response.UserResponse;
import com.monexus.finance.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);

    UserResponse toResponse(User user);
}
