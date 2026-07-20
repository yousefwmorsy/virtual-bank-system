package com.example.userservice.mappers;

import com.example.userservice.dtos.*;
import com.example.userservice.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRegister userRegister);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "username", source = "name")
    UserRegisterResponse toDto(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "username", source = "name")
    UserLoginResponse toLoginDto(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "username", source = "name")
    UserProfileResponse toProfileDto(User user);
}