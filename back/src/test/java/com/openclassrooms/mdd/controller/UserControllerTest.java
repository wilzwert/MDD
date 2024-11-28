package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.UpdateUserDto;
import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.mapper.SubscriptionMapper;
import com.openclassrooms.mdd.mapper.UserMapper;
import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Nested
    class UserTest {

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
            user.setUsername("username");
            user.setEmail("test@example.com");

            UserDto userDto = new UserDto();
            userDto.setId(1);
            userDto.setUsername("username");
            userDto.setEmail("test@example.com");

            Principal principal = mock(Principal.class);
            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userMapper.userToUserDto(user)).thenReturn(userDto);

            UserDto responseUserDto = userController.me(principal);

            assertThat(responseUserDto).isEqualTo(userDto);
        }

        @Test
        public void shouldDeleteCurrentUser() {
            User user = new User();
            user.setId(1);
            user.setUsername("username");
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

        @Test
        public void shouldThrowResponseStatusExceptionNotFoundOnUpdateWhenUserNotFound() {
            UpdateUserDto requestUserUpdate = new UpdateUserDto();
            requestUserUpdate.setUsername("testuser");
            requestUserUpdate.setEmail("test@example.com");
            Principal principal = mock(Principal.class);
            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.update(requestUserUpdate, principal));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldThrowResponseStatusExceptionConflictOnUpdateWhenUsernameOrEmailExists() {
            UpdateUserDto requestUserUpdate = new UpdateUserDto();
            requestUserUpdate.setUsername("testuser");
            requestUserUpdate.setEmail("test@example.com");
            User user = new User();
            User updateUser = new User();
            Principal principal = mock(Principal.class);
            when(principal.getName()).thenReturn("test@example.com");
            when(userMapper.updateUserDtoToUser(requestUserUpdate)).thenReturn(updateUser);
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userService.updateUser(user, updateUser)).thenThrow(EntityExistsException.class);
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.update(requestUserUpdate, principal));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        public void shouldUpdateUser() {
            UpdateUserDto requestUserUpdate = new UpdateUserDto();
            requestUserUpdate.setUsername("othertestuser");
            requestUserUpdate.setEmail("testother@example.com");
            User user = new User();
            User updateUser = new User();
            Principal principal = mock(Principal.class);
            UserDto userDto = new UserDto();
            userDto.setId(1);
            userDto.setUsername("othertestuser");
            userDto.setEmail("testother@example.com");

            when(principal.getName()).thenReturn("test@example.com");
            when(userMapper.updateUserDtoToUser(requestUserUpdate)).thenReturn(updateUser);
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userService.updateUser(user, updateUser)).thenReturn(user);
            when(userMapper.userToUserDto(user)).thenReturn(userDto);

            UserDto responseUserDto = userController.update(requestUserUpdate, principal);

            verify(userMapper).updateUserDtoToUser(requestUserUpdate);
            verify(userService).findUserByEmail("test@example.com");
            verify(userService).updateUser(user, updateUser);
            verify(userMapper).userToUserDto(user);

            assertThat(responseUserDto).isEqualTo(userDto);
        }
    }

    @Nested
    class SubscriptionsTest {

        @Test
        public void shouldThrowResponseStatusExceptionNotFoundOnMeWhenUserNotFound() {
            Principal principal = mock(Principal.class);
            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.subscriptions(principal));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldGetCurrentUserSubscriptions() {
            User user = new User();
            user.setId(1);
            user.setUsername("username");
            user.setEmail("test@example.com");
            Topic topic1 = new Topic().setId(1).setTitle("Topic title");
            Topic topic2 = new Topic().setId(2).setTitle("Topic title2");
            Subscription subscription1 = new Subscription().setUser(user).setTopic(topic1).setCreatedAt(LocalDateTime.now());
            Subscription subscription2 = new Subscription().setUser(user).setTopic(topic2).setCreatedAt(LocalDateTime.now());
            List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
            user.setSubscriptions(subscriptions);

            TopicDto topicDto = new TopicDto();
            topicDto.setId(1);
            topicDto.setTitle("Topic title");
            topicDto.setCreatedAt(LocalDateTime.now());
            TopicDto topicDto2 = new TopicDto();
            topicDto2.setId(2);
            topicDto2.setTitle("Topic title2");
            topicDto2.setCreatedAt(LocalDateTime.now());

            SubscriptionDto subscriptionDto1 = new SubscriptionDto();
            subscriptionDto1.setUserId(1);
            subscriptionDto1.setTopic(topicDto);
            SubscriptionDto subscriptionDto2 = new SubscriptionDto();
            subscriptionDto2.setUserId(1);
            subscriptionDto2.setTopic(topicDto2);

            List<SubscriptionDto> subscriptionDtos = Arrays.asList(subscriptionDto1, subscriptionDto2);

            Principal principal = mock(Principal.class);
            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userService.getSubscriptionsByUser(user)).thenReturn(subscriptions);
            when(subscriptionMapper.subscriptionToSubscriptionDto(subscriptions)).thenReturn(subscriptionDtos);

            List<SubscriptionDto> responseSubscriptionDto = userController.subscriptions(principal);

            verify(userService).findUserByEmail("test@example.com");
            verify(userService).getSubscriptionsByUser(user);
            verify(subscriptionMapper).subscriptionToSubscriptionDto(subscriptions);

            assertThat(responseSubscriptionDto).isEqualTo(subscriptionDtos);
        }
    }
}
