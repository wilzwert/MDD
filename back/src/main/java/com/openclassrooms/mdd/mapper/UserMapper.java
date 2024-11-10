package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {SubscriptionMapper.class})
public interface UserMapper {

    UserDto userToUserDTO(User user);

    User registerUserDtoToUser(RegisterUserDto userDto);
}