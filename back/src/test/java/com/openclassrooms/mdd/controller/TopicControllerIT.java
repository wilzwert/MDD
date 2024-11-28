package com.openclassrooms.mdd.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.*;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.model.*;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.repository.*;
import com.openclassrooms.mdd.repository.TopicRepository;
import org.assertj.core.groups.Tuple;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
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
public class TopicControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private PostRepository postRepository;

    @Nested
    class TopicControllerRetrievalIT {

        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/api/topics"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        public void shouldGetAllTopics() throws Exception {
            LocalDateTime date = LocalDateTime.parse("2024-11-08T10:00:00", DateTimeFormatter.ISO_DATE_TIME);
            User user = new User().setId(1).setEmail("test@test.com").setUsername("test");
            Topic topic1 = new Topic().setId(1).setTitle("Topic title 1").setDescription("topic description 1").setCreator(user).setCreatedAt(date).setUpdatedAt(date);
            Topic topic2 = new Topic().setId(2).setTitle("Topic title 2").setDescription("topic description 2").setCreator(user).setCreatedAt(date).setUpdatedAt(date);

            when(topicRepository.findAll()).thenReturn(Arrays.asList(topic1, topic2));

            MvcResult result = mockMvc.perform(get("/api/topics"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<TopicDto> responseTopics = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TopicDto>>() {});
            assertThat(responseTopics.size()).isEqualTo(2);

            assertThat(responseTopics).extracting(TopicDto::getTitle).containsExactly("Topic title 1", "Topic title 2" );
            assertThat(responseTopics).extracting(TopicDto::getDescription).containsExactly("topic description 1", "topic description 2");
            assertThat(responseTopics).extracting(TopicDto::getCreatedAt).containsExactly(date, date);
            assertThat(responseTopics).extracting(TopicDto::getUpdatedAt).containsExactly(date, date);
        }

        @Test
        @WithMockUser
        public void shouldReturnNotFoundWhenTopicNotFound() throws Exception {
            when(topicRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/topics/{id}", 1))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        public void shouldReturnBadRequestWhenBadId() throws Exception {
            mockMvc.perform(get("/api/topics/badId"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        public void shouldReturnTopic() throws Exception {
            LocalDateTime date = LocalDateTime.parse("2024-11-08T10:00:00", DateTimeFormatter.ISO_DATE_TIME);
            User user = new User().setId(1).setEmail("test@test.com").setUsername("test");
            Topic topic = new Topic().setId(1).setTitle("Topic title 1").setDescription("Topic description 1").setCreator(user).setCreatedAt(date).setUpdatedAt(date);

            when(topicRepository.findById(anyInt())).thenReturn(Optional.of(topic));

            MvcResult result = mockMvc.perform(get("/api/topics/{id}", 1))
                    .andExpect(status().isOk())
                    .andReturn();
            TopicDto responseTopicDto = objectMapper.readValue(result.getResponse().getContentAsString(), TopicDto.class);

            assertThat(responseTopicDto.getTitle()).isEqualTo("Topic title 1");
            assertThat(responseTopicDto.getDescription()).isEqualTo("Topic description 1");
            assertThat(responseTopicDto.getCreatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responseTopicDto.getUpdatedAt()).isEqualTo("2024-11-08T10:00:00");
        }
    }

    @Nested
    class TopicControllerCreationIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(post("/api/topics"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnBadRequestWhenRequestBodyInvalid() throws Exception {
            CreateTopicDto createTopicDto = new CreateTopicDto();

            mockMvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createTopicDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            CreateTopicDto createTopicDto = new CreateTopicDto();
            createTopicDto.setTitle("Topic title");
            createTopicDto.setDescription("Topic description");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createTopicDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldCreateTopic() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");

            CreateTopicDto createTopicDto = new CreateTopicDto();
            createTopicDto.setTitle("Topic title");
            createTopicDto.setDescription("Topic description");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.save(any(Topic.class))).thenAnswer(i -> i.getArgument(0));

            MvcResult result = mockMvc.perform(post("/api/topics").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createTopicDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            TopicDto responseTopicDto = objectMapper.readValue(result.getResponse().getContentAsString(), TopicDto.class);
            assertThat(responseTopicDto.getTitle()).isEqualTo("Topic title");
            assertThat(responseTopicDto.getDescription()).isEqualTo("Topic description");
        }
    }

    // TODO
    @Nested
    class TopicControllerUpdateIT {

    }

    // TODO
    @Nested
    class TopicControllerDeleteIT {

    }

    @Nested
    class TopicControllerPostRetrievalIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/api/topics/1/posts"))
                    .andExpect(status().isForbidden());
        }
        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundWhenTopicNotFound() throws Exception {
            when(topicRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/topics/1/posts"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnEmptyListWhenTopicHasNoPost() throws Exception {
            Topic topic = new Topic().setId(1).setTitle("Topic title");
            when(topicRepository.findById(anyInt())).thenReturn(Optional.of(topic));
            when(postRepository.findByTopic(topic)).thenReturn(new ArrayList<>());

            MvcResult result = mockMvc.perform(get("/api/topics/1/posts"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<PostDto> responsePosts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PostDto>>() {});
            assertThat(responsePosts).isEmpty();
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnPostList() throws Exception {
            LocalDateTime now = LocalDateTime.now();
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");
            Topic topic = new Topic().setId(1).setTitle("Topic title");
            Post post1 = new Post().setId(1).setTopic(topic).setAuthor(user).setTitle("Post title 1").setContent("Post content 1").setCreatedAt(now).setUpdatedAt(now);
            Post post2 = new Post().setId(2).setTopic(topic).setAuthor(user).setTitle("Post title 2").setContent("Post content 2").setCreatedAt(now).setUpdatedAt(now);

            when(topicRepository.findById(anyInt())).thenReturn(Optional.of(topic));
            when(postRepository.findByTopic(topic)).thenReturn(Arrays.asList(post1, post2));

            MvcResult result = mockMvc.perform(get("/api/topics/1/posts"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<PostDto> responsePosts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PostDto>>() {});
            assertThat(responsePosts).hasSize(2);
            assertThat(responsePosts).extracting(PostDto::getId).containsExactly(1, 2);
            assertThat(responsePosts).extracting(PostDto::getTitle).containsExactly("Post title 1", "Post title 2");
            assertThat(responsePosts).extracting(PostDto::getContent).containsExactly("Post content 1", "Post content 2");
            assertThat(responsePosts).extracting(PostDto::getCreatedAt).containsExactly(now, now);
            assertThat(responsePosts).extracting(PostDto::getUpdatedAt).containsExactly(now, now);
            assertThat(responsePosts).extracting(PostDto::getAuthor).asInstanceOf(LIST)
                    .extracting("id", "username", "email")
                    .containsExactly(
                            Tuple.tuple(1,  "test", "test@example.com"),
                            Tuple.tuple(1,  "test", "test@example.com")
                    );
        }
    }

    @Nested
    class TopicControllerSubscriptionCreationIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(post("/api/topics/1/subscription"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        public void shouldReturnBadRequestWhenBadId() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            mockMvc.perform(post("/api/topics/badId/subscription"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundWhenTopicNotFound() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(1)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldCreateSubscription() throws Exception {
            LocalDateTime now = LocalDateTime.now();
            Topic topic = new Topic().setId(1).setTitle("Topic title").setDescription("Topic description").setCreatedAt(now).setUpdatedAt(now);
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));
            when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

            MvcResult result = mockMvc.perform(post("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            SubscriptionDto responseSubscriptionDto = objectMapper.readValue(result.getResponse().getContentAsString(), SubscriptionDto.class);
            assertThat(responseSubscriptionDto.getTopic().getId()).isEqualTo(1);
            assertThat(responseSubscriptionDto.getTopic().getTitle()).isEqualTo("Topic title");
            assertThat(responseSubscriptionDto.getTopic().getDescription()).isEqualTo("Topic description");
            assertThat(responseSubscriptionDto.getTopic().getCreatedAt()).isEqualTo(now);
            assertThat(responseSubscriptionDto.getTopic().getUpdatedAt()).isEqualTo(now);
            assertThat(responseSubscriptionDto.getUserId()).isEqualTo(1);
        }
    }

    @Nested
    class TopicControllerSubscriptionDeletionIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(delete("/api/topics/1/subscription"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        public void shouldReturnBadRequestWhenBadId() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            mockMvc.perform(delete("/api/topics/badId/subscription"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundWhenTopicNotFound() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(1)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundWhenSubscriptionNotFound() throws Exception {
            Topic topic = new Topic().setId(1).setTitle("Topic title").setDescription("Topic description");
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test").setSubscriptions(new ArrayList<>());

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));

            mockMvc.perform(delete("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldDeleteSubscription() throws Exception {
            Topic topic = new Topic().setId(1).setTitle("Topic title").setDescription("Topic description");
            User user = new User().setId(1).setEmail("test@example.com").setUsername("test");
            Subscription subscription = new Subscription().setTopic(topic).setUser(user);
            user.setSubscriptions(Collections.singletonList(subscription));

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));

            mockMvc.perform(delete("/api/topics/1/subscription").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
