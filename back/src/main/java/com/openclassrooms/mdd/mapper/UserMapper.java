package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.request.UpdateUserDto;
import com.openclassrooms.mdd.model.User;
import org.mapstruct.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    UserDto userToUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    User registerUserDtoToUser(RegisterUserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    User updateUserDtoToUser(UpdateUserDto userDto);
}