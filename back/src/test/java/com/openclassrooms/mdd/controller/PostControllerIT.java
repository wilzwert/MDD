package com.openclassrooms.mdd.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mdd.dto.request.CreateOrUpdateCommentDto;
import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.CommentDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.model.*;
import com.openclassrooms.mdd.repository.CommentRepository;
import com.openclassrooms.mdd.repository.PostRepository;
import com.openclassrooms.mdd.repository.TopicRepository;
import com.openclassrooms.mdd.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
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
public class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Nested
    class PostControllerRetrievalIT {

        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/api/posts"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldGetAllPostsForUser() throws Exception {
            LocalDateTime date = LocalDateTime.parse("2024-11-08T10:00:00", DateTimeFormatter.ISO_DATE_TIME);
            User user = new User().setId(1).setEmail("test@example.com").setUserName("test");
            Topic topic1 = new Topic().setId(1).setTitle("test topic").setDescription("test topic description");
            Topic topic2 = new Topic().setId(2).setTitle("second test topic").setDescription("second test topic description");
            Subscription subscription1 = new Subscription().setTopic(topic1).setUser(user);
            Subscription subscription2 = new Subscription().setTopic(topic1).setUser(user);
            List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
            user.setSubscriptions(subscriptions);

            Post post1 = new Post().setId(1).setContent("Post content 1").setTitle("Post title 1").setTopic(topic1).setAuthor(user).setCreatedAt(date).setUpdatedAt(date);
            Post post2 = new Post().setId(2).setContent("Post content 2").setTitle("Post title 2").setTopic(topic2).setAuthor(user).setCreatedAt(date).setUpdatedAt(date);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(postRepository.findByTopicIn(anyList(), any(Sort.class))).thenReturn(Arrays.asList(post1, post2));

            MvcResult result = mockMvc.perform(get("/api/posts"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<PostDto> responsePosts = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PostDto>>() {});
            assertThat(responsePosts.size()).isEqualTo(2);
            assertThat(responsePosts.getFirst().getTitle()).isEqualTo("Post title 1");
            assertThat(responsePosts.getFirst().getContent()).isEqualTo("Post content 1");
            assertThat(responsePosts.getFirst().getCreatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responsePosts.getFirst().getUpdatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responsePosts.getFirst().getAuthor().getId()).isEqualTo(1);
            assertThat(responsePosts.getFirst().getAuthor().getUserName()).isEqualTo("test");
            assertThat(responsePosts.getFirst().getTopic().getId()).isEqualTo(1);
            assertThat(responsePosts.getFirst().getTopic().getTitle()).isEqualTo("test topic");

            assertThat(responsePosts.get(1).getTitle()).isEqualTo("Post title 2");
            assertThat(responsePosts.get(1).getContent()).isEqualTo("Post content 2");
            assertThat(responsePosts.get(1).getCreatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responsePosts.get(1).getCreatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responsePosts.get(1).getAuthor().getId()).isEqualTo(1);
            assertThat(responsePosts.get(1).getAuthor().getUserName()).isEqualTo("test");
            assertThat(responsePosts.get(1).getTopic().getId()).isEqualTo(2);
            assertThat(responsePosts.get(1).getTopic().getTitle()).isEqualTo("second test topic");
        }

        @Test
        @WithMockUser
        public void shouldReturnNotFoundWhenPostNotFound() throws Exception {
            when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/posts/{id}", 1))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        public void shouldReturnBadRequestWhenBadId() throws Exception {
            mockMvc.perform(get("/api/posts/badId"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        public void shouldReturnPost() throws Exception {
            LocalDateTime date = LocalDateTime.parse("2024-11-08T10:00:00", DateTimeFormatter.ISO_DATE_TIME);
            User user = new User().setId(1).setEmail("test@test.com").setUserName("test");
            Post post = new Post().setId(1).setContent("Post content 1").setTitle("Post title 1").setAuthor(user).setCreatedAt(date).setUpdatedAt(date);

            when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

            MvcResult result = mockMvc.perform(get("/api/posts/{id}", 1))
                    .andExpect(status().isOk())
                    .andReturn();
            PostDto responsePostDto = objectMapper.readValue(result.getResponse().getContentAsString(), PostDto.class);

            assertThat(responsePostDto.getTitle()).isEqualTo("Post title 1");
            assertThat(responsePostDto.getContent()).isEqualTo("Post content 1");
            assertThat(responsePostDto.getCreatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responsePostDto.getUpdatedAt()).isEqualTo("2024-11-08T10:00:00");
            assertThat(responsePostDto.getAuthor().getId()).isEqualTo(1);
            assertThat(responsePostDto.getAuthor().getUserName()).isEqualTo("test");
        }

    }

    @Nested
    class PostControllerCreationIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(post("/api/posts"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnBadRequestWhenRequestBodyInvalid() throws Exception {
            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTopicId(1);

            mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTopicId(1);
            createPostDto.setTitle("Post title");
            createPostDto.setContent("Post content");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnBadRequestWhenTopicNotFound() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUserName("test");
            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTopicId(1);
            createPostDto.setTitle("Post title");
            createPostDto.setContent("Post content");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldCreatePost() throws Exception {
            User user = new User().setId(1).setEmail("test@example.com").setUserName("test");
            Topic topic = new Topic().setId(1).setTitle("Post title");

            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTopicId(1);
            createPostDto.setTitle("Post title");
            createPostDto.setContent("Post content");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(topicRepository.findById(anyInt())).thenReturn(Optional.of(topic));
            when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

            MvcResult result = mockMvc.perform(post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createPostDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            PostDto responsePostDto = objectMapper.readValue(result.getResponse().getContentAsString(), PostDto.class);
            assertThat(responsePostDto.getTitle()).isEqualTo("Post title");
            assertThat(responsePostDto.getContent()).isEqualTo("Post content");
            assertThat(responsePostDto.getAuthor().getId()).isEqualTo(1);
            assertThat(responsePostDto.getAuthor().getUserName()).isEqualTo("test");
        }
    }

    // TODO
    @Nested
    class PostControllerUpdateIT {

    }

    // TODO
    @Nested
    class PostControllerDeleteIT {

    }

    @Nested
    class PostControllerCommentRetrievalIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(get("/api/posts/{id}/comments", 1))
                    .andExpect(status().isForbidden());

        }

        @Test
        @WithMockUser
        public void shouldReturnNotFoundWhenPostNotFound() throws Exception {

            when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/posts/{id}/comments", 1))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        public void shouldReturnPostComments() throws Exception {
            User user = new User().setId(1).setUserName("test");
            Post post = new Post().setId(1).setTitle("Post title").setContent("Post content");
            Comment comment1 = new Comment().setId(1).setContent("Comment content").setPost(post).setAuthor(user);
            Comment comment2 = new Comment().setId(2).setContent("Comment content 2").setPost(post).setAuthor(user);
            when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
            when(commentRepository.findCommentsByPost(post)).thenReturn(Arrays.asList(comment1, comment2));

            MvcResult result = mockMvc.perform(get("/api/posts/{id}/comments", 1))
                    .andExpect(status().isOk())
                    .andReturn();

            List<CommentDto> responseCommentDtos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CommentDto>>(){});
            assertThat(responseCommentDtos.size()).isEqualTo(2);
            assertThat(responseCommentDtos.getFirst().getContent()).isEqualTo("Comment content");
            assertThat(responseCommentDtos.getFirst().getAuthor().getId()).isEqualTo(1);
            assertThat(responseCommentDtos.getFirst().getAuthor().getUserName()).isEqualTo("test");
            assertThat(responseCommentDtos.get(1).getContent()).isEqualTo("Comment content 2");
            assertThat(responseCommentDtos.get(1).getAuthor().getId()).isEqualTo(1);
            assertThat(responseCommentDtos.get(1).getAuthor().getUserName()).isEqualTo("test");
        }
    }

    @Nested
    class PostControllerCommentCreationIT {
        @Test
        public void shouldReturnForbiddenWhenNotLoggedIn() throws Exception {
            mockMvc.perform(post("/api/posts/{id}/comments", 1))
                    .andExpect(status().isForbidden());

        }

        @Test
        @WithMockUser
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            CreateOrUpdateCommentDto createOrUpdateCommentDto = new CreateOrUpdateCommentDto();
            createOrUpdateCommentDto.setContent("Comment content");

            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/posts/{id}/comments", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createOrUpdateCommentDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnBadRequestWhenRequestBodyInvalid() throws Exception {
            CreateOrUpdateCommentDto createOrUpdateCommentDto = new CreateOrUpdateCommentDto();

            mockMvc.perform(post("/api/posts/{id}/comments", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createOrUpdateCommentDto)))
                    .andExpect(status().isBadRequest());

        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldReturnNotFoundWhenPostNotFound() throws Exception {
            User user = new User().setId(1).setUserName("test").setEmail("test@example.com");
            CreateOrUpdateCommentDto createOrUpdateCommentDto = new CreateOrUpdateCommentDto();
            createOrUpdateCommentDto.setContent("Comment content");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/posts/{id}/comments", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createOrUpdateCommentDto)))
                    .andExpect(status().isNotFound());

        }

        @Test
        @WithMockUser(username = "test@example.com")
        public void shouldCreateComment() throws Exception {
            User user = new User().setId(1).setUserName("test").setEmail("test@example.com");
            Post post = new Post().setId(1).setTitle("Post title").setContent("Post content");
            CreateOrUpdateCommentDto createOrUpdateCommentDto = new CreateOrUpdateCommentDto();
            createOrUpdateCommentDto.setContent("Comment content");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
            when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

            MvcResult result = mockMvc.perform(post("/api/posts/{id}/comments", 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createOrUpdateCommentDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CommentDto responseCommentDto = objectMapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);
            assertThat(responseCommentDto.getContent()).isEqualTo("Comment content");
            assertThat(responseCommentDto.getAuthor().getId()).isEqualTo(1);
            assertThat(responseCommentDto.getAuthor().getUserName()).isEqualTo("test");
        }

    }
}
