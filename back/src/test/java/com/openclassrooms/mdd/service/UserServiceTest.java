package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.SubscriptionRepository;
import com.openclassrooms.mdd.repository.TopicRepository;
import com.openclassrooms.mdd.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/11/2024
 * Time:14:53
 */

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Nested
    class EncodePasswordTest {
        @Test
        public void shouldReturnEncodedPassword() {
            when(passwordEncoder.encode("Abcd!1234")).thenReturn("encodedpassword");

            assertThat(userService.encodePassword("Abcd!1234")).isEqualTo("encodedpassword");
        }
    }

    @Nested
    class GetUserTest {
        @Test
        public void shouldReturnEmptyOptionalWhenUserNotFoundByEmail() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            assertThat(userService.findUserByEmail("test@example.com")).isEmpty();

            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        public void shouldReturnUserFoundByEmail() {
            User user = new User().setId(1).setEmail("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            assertThat(userService.findUserByEmail("test@example.com")).isPresent().get().isEqualTo(user);

            verify(userRepository).findByEmail("test@example.com");
        }
    }

    @Nested
    class RegisterUserTest {
        @Test
        public void shouldThrowEntityExistsExceptionWhenEmailAlreadyExists() {
            User user = new User().setEmail("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            assertThrows(EntityExistsException.class, () -> userService.registerUser(user));

            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        public void shouldRegisterUser() {
            User user = new User().setEmail("test@example.com").setPassword("password");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
            when(userRepository.save(user)).thenReturn(user);

            User registeredUser =  userService.registerUser(user);

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).encode("password");
            verify(userRepository).save(user);

            assertThat(registeredUser).isNotNull().isEqualTo(user);

        }
    }

    @Nested
    class AuthenticateUserTest {
        @Test
        public void shouldThrowAuthenticationExceptionWhenUserNotFound() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            AuthenticationException exception = assertThrows(AuthenticationException.class, () -> userService.authenticateUser("test@example.com", "password"));
            assertThat(exception.getMessage()).isEqualTo("User not found");

            verify(userRepository).findByEmail("test@example.com");
        }
        @Test
        public void shouldThrowAuthenticationExceptionWhenAuthenticationManagerFails() {
            User user = new User().setEmail("test@example.com").setPassword("password").setUserName("test");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new AuthenticationException(""){});

            assertThrows(AuthenticationException.class, () -> userService.authenticateUser("test@example.com", "password"));

            verify(userRepository).findByEmail("test@example.com");
            verify(authenticationManager).authenticate(any(Authentication.class));
        }


        @Test
        public void shouldAuthenticateUser() {
            User user = new User().setEmail("test@example.com").setPassword("password");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(new UsernamePasswordAuthenticationToken("test", "password"));

            User authenticatedUser =  userService.authenticateUser("test@example.com", "password");

            verify(userRepository).findByEmail("test@example.com");
            verify(authenticationManager).authenticate(any(Authentication.class));

            assertThat(authenticatedUser).isNotNull().isEqualTo(user);

        }
    }

    @Nested
    class DeleteUserTest {
        @Test
        public void shouldDeleteUser() {
            User user = new User().setEmail("test@example.com").setPassword("password");
            doNothing().when(userRepository).delete(user);

            assertDoesNotThrow(() -> userService.deleteUser(user));

            verify(userRepository).delete(user);
        }
    }

    @Nested
    class SubscriptionCreationTest {
        @Test
        public void shouldThrowEntityNotFoundExceptionWhenTopicNotFound() {
            User user = new User().setEmail("test@example.com").setPassword("password").setUserName("test");
            when(topicRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.subscribe(user, 1));

            verify(topicRepository).findById(1);
        }

        @Test
        public void shouldCreateSubscription() {
            User user = new User().setEmail("test@example.com").setPassword("password").setUserName("test");
            Topic topic = new Topic().setId(1).setTitle("Topic title");

            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));
            when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

            Subscription createdSubscription = userService.subscribe(user, 1);

            assertThat(createdSubscription).isNotNull();
            assertThat(createdSubscription.getTopic()).isEqualTo(topic);
            assertThat(createdSubscription.getUser()).isEqualTo(user);

            verify(topicRepository).findById(1);
            verify(subscriptionRepository).save(any(Subscription.class));
        }
    }

    @Nested
    class SubscriptionDeletionTest {
        @Test
        public void shouldThrowEntityNotFoundExceptionWhenTopicNotFound() {
            User user = new User().setEmail("test@example.com").setPassword("password").setUserName("test");
            when(topicRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.unSubscribe(user, 1));

            verify(topicRepository).findById(1);
        }

        @Test
        public void shouldThrowEntityNotFoundExceptionWhenParticipationNotFound() {
            User user = new User().setEmail("test@example.com").setPassword("password").setUserName("test").setSubscriptions(new ArrayList<>());
            Topic topic = new Topic().setId(1).setTitle("Topic title");
            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));

            assertThrows(EntityNotFoundException.class, () -> userService.unSubscribe(user, 1));

            verify(topicRepository).findById(1);
        }

        @Test
        public void shouldDeleteSubscription() {
            User user = new User().setEmail("test@example.com").setPassword("password").setUserName("test");
            Topic topic = new Topic().setId(1).setTitle("Topic title");
            Subscription subscription = new Subscription().setUser(user).setTopic(topic).setCreatedAt(LocalDateTime.now());
            user.setSubscriptions(Collections.singletonList(subscription));

            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));
            doNothing().when(subscriptionRepository).delete(subscription);

            userService.unSubscribe(user, 1);

            verify(topicRepository).findById(1);
            verify(subscriptionRepository).delete(subscription);
        }
    }

    @Nested
    class GetUserSubscriptionsTest {
        @Test
        public void shouldThrowNullPointerExceptionWhenUserIsNull() {
            assertThrows(NullPointerException.class, () -> userService.getSubscriptionsByUser(null));
        }

        @Test
        public void shouldReturnEmptyListWhenUserHasNoSubscriptions() {
            User user = new User().setId(1).setEmail("test@example.com").setSubscriptions(new ArrayList<>());
            assertThat(userService.getSubscriptionsByUser(user)).hasSize(0);
        }

        @Test
        public void shouldReturnSubscriptionsList() {
            User user = new User().setId(1).setEmail("test@example.com");
            Topic topic1 = new Topic().setId(1).setTitle("Topic title");
            Topic topic2 = new Topic().setId(2).setTitle("Topic title2");
            Subscription subscription1 = new Subscription().setUser(user).setTopic(topic1).setCreatedAt(LocalDateTime.now());
            Subscription subscription2 = new Subscription().setUser(user).setTopic(topic2).setCreatedAt(LocalDateTime.now());
            user.setSubscriptions(Arrays.asList(subscription1, subscription2));

            List<Subscription> subscriptions = userService.getSubscriptionsByUser(user);
            assertThat(subscriptions).hasSize(2);
            assertThat(subscriptions.get(0).getUser()).isEqualTo(user);
            assertThat(subscriptions.get(0).getTopic()).isEqualTo(topic1);
            assertThat(subscriptions.get(1).getUser()).isEqualTo(user);
            assertThat(subscriptions.get(1).getTopic()).isEqualTo(topic2);
        }
    }
}
