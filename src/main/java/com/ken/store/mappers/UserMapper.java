package com.ken.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.ken.store.dtos.ChangePasswordRequest;
import com.ken.store.dtos.RegisterUserRequest;
import com.ken.store.dtos.UpdateUserRequest;
import com.ken.store.dtos.UserDto;
import com.ken.store.entities.User;

// componentModel = "spring": so that spring can create beans for it
@Mapper(componentModel = "spring")
// We don't need to write mapper functions
// mapstruct will automatically construct them for us
// you can find it when running the application at
// "target\generated-sources\annotations\com\ken\store\mappers\UserMapperImpl.java"
public interface UserMapper {
    /*
     * @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
     */
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest request, @MappingTarget User user);
}
