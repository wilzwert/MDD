package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.model.User;
import org.mapstruct.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {SubscriptionMapper.class})
public interface UserMapper {

    UserDto userToUserDto(User user);

    @Named("userToUserDtoWithoutSubscriptions")
    @UserWithoutSubscriptions
    @Mapping( target = "subscriptions", ignore = true)
    UserDto userToUserDtoWithoutSubscriptions(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "topics", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "comments", ignore = true)
    User registerUserDtoToUser(RegisterUserDto userDto);
}