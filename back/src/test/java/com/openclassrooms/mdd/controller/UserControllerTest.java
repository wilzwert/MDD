package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.mapper.PostMapper;
import com.openclassrooms.mdd.mapper.TopicMapper;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.security.service.UserDetailsImpl;
import com.openclassrooms.mdd.service.PostService;
import com.openclassrooms.mdd.service.TopicService;
import com.openclassrooms.mdd.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:11:59
 */
@Tag("Topic")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Test
    public void shouldThrowResponseStatusExceptionNotFoundOnMeWhenUserNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.me(principal));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldGetCurrentUserInfo() {
        User user = new User();
        user.setId(1);
        user.setUserName("username");
        user.setEmail("test@example.com");

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setUserName("username");
        userDto.setEmail("test@example.com");
        TopicDto topicDto1 = new TopicDto();
        topicDto1.setId(1);
        topicDto1.setTitle("title");
        TopicDto topicDto2 = new TopicDto();
        topicDto2.setId(2);
        topicDto2.setTitle("title2");
        SubscriptionDto subscriptionDto1 = new SubscriptionDto();
        subscriptionDto1.setUserId(1);
        subscriptionDto1.setTopic(topicDto1);
        SubscriptionDto subscriptionDto2 = new SubscriptionDto();
        subscriptionDto2.setUserId(1);
        subscriptionDto2.setTopic(topicDto2);
        userDto.setSubscriptions(Arrays.asList(subscriptionDto1, subscriptionDto2));

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDto);

        UserDto responseUserDto = userController.me(principal);

        assertThat(responseUserDto).isEqualTo(userDto);
    }

    @Test
    public void shouldDeleteCurrentUser() {
        User user = new User();
        user.setId(1);
        user.setUserName("username");
        user.setEmail("test@example.com");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(userService).deleteUser(user);

        ResponseEntity<?> responseEntity = userController.deleteCurrentUser(principal);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
        public void shouldThrowResponseStatusExceptionNotFoundOnDeleteWhenUserNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");
        when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.deleteCurrentUser(principal));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
