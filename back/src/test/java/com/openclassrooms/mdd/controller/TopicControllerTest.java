package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.mapper.PostMapper;
import com.openclassrooms.mdd.mapper.SubscriptionMapper;
import com.openclassrooms.mdd.mapper.TopicMapper;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.service.PostService;
import com.openclassrooms.mdd.service.TopicService;
import com.openclassrooms.mdd.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:11:59
 */
@Tag("Topic")
@ExtendWith(MockitoExtension.class)
public class TopicControllerTest {

    @InjectMocks
    private TopicController topicController;

    @Mock
    private TopicService topicService;

    @Mock
    private UserService userService;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private PostService postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private Principal principal;

    @Nested
    class TopicControllerRetrievalTest {
        @Test
        public void shouldFindTopicById() {
            Topic topic = new Topic();
            topic.setId(1);
            topic.setTitle("Test Topic");
            TopicDto topicDto = new TopicDto();
            topicDto.setId(1);

            when(topicService.getTopicById(1)).thenReturn(Optional.of(topic));
            when(topicMapper.topicToTopicDto(topic)).thenReturn(topicDto);

            TopicDto responseTopicDto = topicController.findById("1");

            assertThat(responseTopicDto).isEqualTo(topicDto);
        }

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            assertThrows(NumberFormatException.class, () -> topicController.findById("badId1"));
        }

        @Test
        public void shouldReturnNotFoundWhenNotFound() {
            when(topicService.getTopicById(1)).thenReturn(Optional.empty());

            assertThrows(
                    ResponseStatusException.class,
                    () -> topicController.findById("1")
            );
        }

        @Test
        public void shouldFindAll() {
            Topic topic1 = new Topic().setId(1).setTitle("Test topic");
            Topic topic2 = new Topic().setId(2).setTitle("Other test topic");
            List<Topic> topics = Arrays.asList(topic1, topic2);

            TopicDto topicDto1 = new TopicDto();
            topicDto1.setId(1);
            topicDto1.setTitle("Test topic");

            TopicDto topicDto2 = new TopicDto();
            topicDto2.setId(2);
            topicDto2.setTitle("Other test topic");

            List<TopicDto> topicDtos = Arrays.asList(topicDto1, topicDto2);

            when(topicService.getAllTopics()).thenReturn(topics);
            when(topicMapper.topicToTopicDto(topics)).thenReturn(topicDtos);

            List<TopicDto> foundTopicDtos = topicController.findAll();

            assertThat(foundTopicDtos).isEqualTo(topicDtos);
        }
    }

    @Nested
    class TopicControllerCreateTest {

        @Test
        public void shouldThrowUnauthorizedResponseStatusExceptionWhenUserNotFound() {
            CreateTopicDto createTopicDto = new CreateTopicDto();
            createTopicDto.setTitle("Test topic");
            createTopicDto.setDescription("Test description");

            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> topicController.createTopic(createTopicDto, principal));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldCreateTopic() {
            LocalDateTime now = LocalDateTime.now();

            CreateTopicDto requestTopicDto = new CreateTopicDto();
            requestTopicDto.setTitle("Test topic");
            requestTopicDto.setDescription("Topic description");

            Topic topic = new Topic();
            topic.setId(1).setTitle("Test topic").setCreatedAt(now).setUpdatedAt(now).setCreator(new User().setId(1));

            TopicDto responseTopicDto = new TopicDto();
            responseTopicDto.setId(1);
            responseTopicDto.setTitle("Test topic");
            responseTopicDto.setDescription("Topic description");
            responseTopicDto.setCreatedAt(now);
            responseTopicDto.setUpdatedAt(now);

            when(topicService.createTopic(any(Topic.class))).thenReturn(topic);
            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(new User().setId(1)));
            when(topicMapper.createTopicDtoToTopic(requestTopicDto)).thenReturn(topic);
            when(topicMapper.topicToTopicDto(topic)).thenReturn(responseTopicDto);

            TopicDto createdTopicDto = topicController.createTopic(requestTopicDto, principal);

            assertThat(createdTopicDto.getId()).isEqualTo(1);
            assertThat(createdTopicDto.getTitle()).isEqualTo("Test topic");
            assertThat(createdTopicDto.getDescription()).isEqualTo("Topic description");
            assertThat(createdTopicDto.getCreatedAt()).isEqualTo(now);
            assertThat(createdTopicDto.getUpdatedAt()).isEqualTo(now);
        }
    }
    /* TODO
    @Nested
    class TopicControllerUpdateTest {
        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            TopicDto requestTopicDto = new TopicDto();
            requestTopicDto.setTitle("Updated test topic");

            ResponseEntity<?> responseEntity = topicController.update("badId1", requestTopicDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldUpdateTopic() {
            TopicDto requestTopicDto = new TopicDto();
            requestTopicDto.setTitle("Updated test topic");

            Topic topic = new Topic();
            topic.setId(1L).setTitle("Test topic").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            TopicDto responseTopicDto = new TopicDto();
            responseTopicDto.setId(topic.getId());
            responseTopicDto.setTitle(topic.getName());
            responseTopicDto.setTeacher_id(topic.getTeacher().getId());
            responseTopicDto.setCreatedAt(topic.getCreatedAt());

            when(topicService.update(1L, topic)).thenReturn(topic);
            when(topicMapper.toEntity(requestTopicDto)).thenReturn(topic);
            when(topicMapper.toDto(topic)).thenReturn(responseTopicDto);

            ResponseEntity<?> responseEntity = topicController.update("1", requestTopicDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(responseTopicDto);
        }
    }*/

    /* TODO
    @Nested
    class TopicControllerDeleteTest {
        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            ResponseEntity<?> responseEntity = topicController.save("badId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnBadRequestWhenTopicNotFound() {
            when(topicService.getById(1L)).thenReturn(null);

            ResponseEntity<?> responseEntity = topicController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldDeleteTopic() {
            Topic topic = new Topic();
            topic.setId(1L).setTitle("Test topic").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            when(topicService.getById(1L)).thenReturn(topic);

            ResponseEntity<?> responseEntity = topicController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }*/

    @Nested
    class TopicControllerPostsRetrievalTest {

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            assertThrows(NumberFormatException.class, () -> topicController.findPosts("badId1"));
        }

        @Test
        public void shouldReturnNotFoundWhenTopicNotFound() {
            when(topicService.getTopicById(1)).thenReturn(Optional.empty());

            assertThrows(
                    ResponseStatusException.class,
                    () -> topicController.findPosts("1")
            );
        }

        @Test
        public void shouldFindAllTopicPosts() {
            Topic topic = new Topic();
            topic.setId(1);
            topic.setTitle("Test Topic");
            TopicDto topicDto = new TopicDto();
            topicDto.setId(1);

            Post post1 = new Post().setId(1).setTitle("Test post").setContent("Test post content");
            Post post2 = new Post().setId(2).setTitle("Other test post").setContent("Other test post content");
            List<Post> posts = Arrays.asList(post1, post2);

            PostDto postDto1 = new PostDto();
            postDto1.setId(1);
            postDto1.setTitle("Test post");
            postDto1.setContent("Test post content");

            PostDto postDto2 = new PostDto();
            postDto2.setId(2);
            postDto2.setTitle("Other test post");
            postDto2.setContent("Other test post content");

            List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

            when(topicService.getTopicById(1)).thenReturn(Optional.of(topic));
            when(postService.getPostsByTopic(topic)).thenReturn(posts);
            when(postMapper.postToPostDto(posts)).thenReturn(postDtos);

            List<PostDto> foundPostDtos = topicController.findPosts("1");

            assertThat(foundPostDtos).isEqualTo(postDtos);
        }
    }

    @Nested
    class TopicControllerSubscribeTest {

        @Test
        public void shouldThrowUnauthorizedResponseStatusExceptionWhenUserNotFound() {
            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> topicController.subscribe("1", principal));

            verify(principal).getName();
            verify(userService).findUserByEmail("test@example.com");

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldThrowNotFoundResponseStatusExceptionWhenTopicNotFound() {
            User user = new User().setId(1).setUserName("test").setEmail("test@example.com");

            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userService.subscribe(user, 1)).thenThrow(EntityNotFoundException.class);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> topicController.subscribe("1", principal));

            verify(principal).getName();
            verify(userService).findUserByEmail("test@example.com");
            verify(userService).subscribe(user, 1);
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldReturnSubscriptionWhenSubscriptionSuccessful() {
            User user = new User().setId(1).setUserName("test").setEmail("test@example.com");
            Topic topic = new Topic().setId(1).setTitle("Test topic").setCreatedAt(LocalDateTime.now());
            Subscription subscription = new Subscription().setTopic(topic).setUser(user);
            SubscriptionDto subscriptionDto = new SubscriptionDto();
            subscriptionDto.setTopic(topicMapper.topicToTopicDto(topic));
            subscriptionDto.setUserId(1);
            subscriptionDto.setCreatedAt(LocalDateTime.now());

            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(userService.subscribe(user, 1)).thenReturn(subscription);
            when(subscriptionMapper.subscriptionToSubscriptionDto(subscription)).thenReturn(subscriptionDto);

            SubscriptionDto responseSubscriptionDto = topicController.subscribe("1", principal);

            verify(principal).getName();
            verify(userService).findUserByEmail("test@example.com");
            verify(userService).subscribe(user, 1);
            verify(subscriptionMapper).subscriptionToSubscriptionDto(subscription);

            assertThat(responseSubscriptionDto).isEqualTo(subscriptionDto);
        }
    }
    /*
    @Nested
    class TopicControllerNoLongerSubscribeTest {
        @Test
        public void shouldReturnBadRequestWhenBadTopicIdFormat() {
            ResponseEntity<?> responseEntity = topicController.noLongerSubscribe("badId1", "1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnBadRequestWhenBadUserIdFormat() {
            ResponseEntity<?> responseEntity = topicController.noLongerSubscribe("1", "badUserId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldNoLongerSubscribe() {
            ResponseEntity<?> responseEntity = topicController.noLongerSubscribe("1", "1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }*/
}
