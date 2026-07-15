package com.example.userservice.mappers;

import com.example.userservice.dtos.UserRegister;
import com.example.userservice.dtos.UserRegisterResponse;
import com.example.userservice.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(UserRegister userRegister);
    UserRegisterResponse toDto(User user);
}
