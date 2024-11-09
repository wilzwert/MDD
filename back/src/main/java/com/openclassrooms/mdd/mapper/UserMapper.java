package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    UserDto userToUserDTO(User user);

    User registerUserDtoToUser(RegisterUserDto userDto);
}