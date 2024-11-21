package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.model.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:39
 */

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Tag("Mapper")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testNullUserToDto() {
        User user = null;

        UserDto userDto = userMapper.userToUserDto(user);

        assertThat(userDto).isNull();
    }

    @Test
    public void testUserToDto() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User()
                .setId(1)
                .setUserName("testuser")
                .setEmail("test@example.com")
                .setCreatedAt(now)
                .setUpdatedAt(now);

        UserDto userDto = userMapper.userToUserDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getUserName()).isEqualTo("testuser");
        assertThat(userDto.getCreatedAt()).isEqualTo(now);
        assertThat(userDto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    public void testNullDtoToUser() {
        RegisterUserDto userDto = null;

        User user = userMapper.registerUserDtoToUser(userDto);

        assertThat(user).isNull();
    }

    @Test
    public void testCreateDtoToUser() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setUserName("testuser");
        userDto.setEmail("test@example.com");
        User user = userMapper.registerUserDtoToUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }
}
