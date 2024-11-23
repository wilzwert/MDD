package com.openclassrooms.mdd.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mdd.dto.request.UpdateUserDto;
import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:10:53
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class UserControllerIT {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final static String ME_URL = "/api/user/me";
    private final static String DELETE_URL = "/api/user/me";
    private final static String SUBSCRIPTIONS_URL = "/api/user/me/subscriptions";

    @Nested
    class UserIT {
        @Test
        public void shouldReturnForbiddenWhenNoAuth() throws Exception {
            mockMvc.perform(get(ME_URL))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundOnGetWhenUserNotFound() throws Exception {
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
            mockMvc.perform(get(ME_URL))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnUserDto() throws Exception {
            User user = new User();
            user.setId(1);
            user.setUserName("username");
            user.setEmail("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            MvcResult result = mockMvc.perform(get(ME_URL)).andExpect(status().isOk()).andReturn();
            UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

            verify(userRepository, times(1)).findByEmail("test@example.com");

            assertThat(userDto.getId()).isEqualTo(1);
            assertThat(userDto.getUserName()).isEqualTo("username");
            assertThat(userDto.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundOnDeleteWhenUserNotFound() throws Exception {
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
            mockMvc.perform(delete(DELETE_URL))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNoContentOnDelete() throws Exception {
            User user = new User();

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            doNothing().when(userRepository).delete(user);

            mockMvc.perform(delete(DELETE_URL)).andExpect(status().isNoContent());
            verify(userRepository, times(1)).findByEmail("test@example.com");
            verify(userRepository, times(1)).delete(user);
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnBadRequestOnUpdateWhenInvalidBody() throws Exception {
            UpdateUserDto updateUserDto = new UpdateUserDto();
            updateUserDto.setEmail("test@example.com");

            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
            mockMvc.perform(put(ME_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundOnUpdateWhenUserNotFound() throws Exception {
            UpdateUserDto updateUserDto = new UpdateUserDto();
            updateUserDto.setUserName("username");
            updateUserDto.setEmail("test@example.com");

            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
            mockMvc.perform(put(ME_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnConflictOnUpdateWhenUserNameExists() throws Exception {
            User user = new User().setId(1).setUserName("username").setEmail("test@example.com");
            User user2 = new User().setId(2).setUserName("otherusername").setEmail("othertest@example.com");

            UpdateUserDto updateUserDto = new UpdateUserDto();
            // change username
            updateUserDto.setUserName("otherusername");
            // update email
            updateUserDto.setEmail("test@example.com");

            // mock current authenticated user retrieval
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            // mock an other user with username sent in update request
            when(userRepository.findByUserName("otherusername")).thenReturn(Optional.of(user2));

            mockMvc.perform(put(ME_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDto)))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnConflictOnUpdateWhenEmailExists() throws Exception {
            User user = new User().setId(1).setUserName("username").setEmail("test@example.com");
            User user2 = new User().setId(2).setUserName("otherusername").setEmail("othertest@example.com");

            UpdateUserDto updateUserDto = new UpdateUserDto();
            // same username
            updateUserDto.setUserName("username");
            // change email
            updateUserDto.setEmail("othertest@example.com");

            // mock current authenticated user retrieval
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            // mock an other user with email sent in update request
            when(userRepository.findByEmail("othertest@example.com")).thenReturn(Optional.of(user2));

            mockMvc.perform(put(ME_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDto)))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnUpdatedUser() throws Exception {
            User user = new User().setId(1).setUserName("username").setEmail("test@example.com");

            UpdateUserDto updateUserDto = new UpdateUserDto();
            // change username
            updateUserDto.setUserName("otherusername");
            // change email
            updateUserDto.setEmail("othertest@example.com");

            // mock current authenticated user retrieval
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            // username and email don't exist in db
            when(userRepository.findByEmail("othertest@example.com")).thenReturn(Optional.empty());
            when(userRepository.findByUserName("otherusername")).thenReturn(Optional.empty());
            when(userRepository.save(user)).thenReturn(user);

            MvcResult responseUser = mockMvc.perform(put(ME_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateUserDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            UserDto userDto = objectMapper.readValue(responseUser.getResponse().getContentAsString(), UserDto.class);
            assertThat(userDto.getId()).isEqualTo(1);
            assertThat(userDto.getUserName()).isEqualTo("otherusername");
            assertThat(userDto.getEmail()).isEqualTo("othertest@example.com");
        }
    }
    @Nested
    class SubscriptionsIT {
        @Test
        public void shouldReturnForbiddenWhenNoAuth() throws Exception {
            mockMvc.perform(get(SUBSCRIPTIONS_URL))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundOnGetSubscriptionsWhenUserNotFound() throws Exception {
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
            mockMvc.perform(get(SUBSCRIPTIONS_URL))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnSubscriptions() throws Exception {
            LocalDateTime now = LocalDateTime.now();

            User user = new User();
            user.setId(1);
            user.setUserName("username");
            user.setEmail("test@example.com");
            Topic topic1 = new Topic().setId(1).setTitle("Topic title");
            Topic topic2 = new Topic().setId(2).setTitle("Topic title2");
            Subscription subscription1 = new Subscription().setUser(user).setTopic(topic1).setCreatedAt(now);
            Subscription subscription2 = new Subscription().setUser(user).setTopic(topic2).setCreatedAt(now);
            List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
            user.setSubscriptions(subscriptions);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            MvcResult result = mockMvc.perform(get(SUBSCRIPTIONS_URL)).andExpect(status().isOk()).andReturn();

            verify(userRepository, times(1)).findByEmail("test@example.com");

            List<SubscriptionDto> responseSubscriptionDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<SubscriptionDto>>() {});
            assertThat(responseSubscriptionDtos.size()).isEqualTo(2);

            assertThat(responseSubscriptionDtos.getFirst().getUserId()).isEqualTo(1);
            assertThat(responseSubscriptionDtos.getFirst().getCreatedAt()).isEqualTo(now);
            assertThat(responseSubscriptionDtos.getFirst().getTopic().getId()).isEqualTo(1);
            assertThat(responseSubscriptionDtos.getFirst().getTopic().getTitle()).isEqualTo("Topic title");

            assertThat(responseSubscriptionDtos.get(1).getUserId()).isEqualTo(1);
            assertThat(responseSubscriptionDtos.get(1).getCreatedAt()).isEqualTo(now);
            assertThat(responseSubscriptionDtos.get(1).getTopic().getId()).isEqualTo(2);
            assertThat(responseSubscriptionDtos.get(1).getTopic().getTitle()).isEqualTo("Topic title2");

        }
    }

}
