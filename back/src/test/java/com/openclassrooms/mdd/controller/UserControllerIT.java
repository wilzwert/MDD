package com.openclassrooms.mdd.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mdd.dto.request.LoginRequestDto;
import com.openclassrooms.mdd.dto.request.RegisterUserDto;
import com.openclassrooms.mdd.dto.response.JwtResponse;
import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.dto.response.UserDto;
import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final static String ME_URL = "/api/user/me";
    private final static String DELETE_URL = "/api/user/me";

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

        Topic topic1 = new Topic().setId(1).setTitle("title").setDescription("description");
        Topic topic2 = new Topic().setId(2).setTitle("title2").setDescription("description2");
        Subscription subscription1 = new Subscription().setUser(user).setTopic(topic1);
        Subscription subscription2 = new Subscription().setUser(user).setTopic(topic2);

        user.setSubscriptions(Arrays.asList(subscription1, subscription2));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        MvcResult result = mockMvc.perform(get(ME_URL)).andExpect(status().isOk()).andReturn();
        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        verify(userRepository, times(1)).findByEmail("test@example.com");

        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getUserName()).isEqualTo("username");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
        assertThat(userDto.getSubscriptions()).hasSize(2);
        SubscriptionDto subscriptionDto1 = userDto.getSubscriptions().getFirst();
        assertThat(subscriptionDto1.getTopic().getId()).isEqualTo(1);
        assertThat(subscriptionDto1.getTopic().getTitle()).isEqualTo("title");
        assertThat(subscriptionDto1.getTopic().getDescription()).isEqualTo("description");
        SubscriptionDto subscriptionDto2 = userDto.getSubscriptions().get(1);
        assertThat(subscriptionDto2.getTopic().getId()).isEqualTo(2);
        assertThat(subscriptionDto2.getTopic().getTitle()).isEqualTo("title2");
        assertThat(subscriptionDto2.getTopic().getDescription()).isEqualTo("description2");
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
t
    }

}
