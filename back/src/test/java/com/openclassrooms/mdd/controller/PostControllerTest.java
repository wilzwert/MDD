package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.mapper.PostMapper;
import com.openclassrooms.mdd.mapper.PostMapper;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.service.PostService;
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
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:11:59
 */
@Tag("Post")
@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private TopicService topicService;

    @Mock
    private UserService userService;

    @Mock
    private PostMapper postMapper;

    @Nested
    class PostControllerFindTest {
        @Test
        public void shouldFindPostById() {
            Post post = new Post();
            post.setId(1);
            post.setTitle("Test Post");
            post.setContent("Test Content");
            PostDto postDto = new PostDto();
            postDto.setId(1);
            postDto.setTitle("Test Post");
            postDto.setContent("Test Content");

            when(postService.getPostById(1)).thenReturn(Optional.of(post));
            when(postMapper.postToPostDTO(post)).thenReturn(postDto);

            PostDto responsePostDto = postController.findById("1");

            assertThat(responsePostDto).isEqualTo(postDto);
        }

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            assertThrows(NumberFormatException.class, () -> postController.findById("badId1"));
//            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnNotFoundWhenNotFound() {
            when(postService.getPostById(1)).thenReturn(Optional.empty());

            assertThrows(
                    ResponseStatusException.class,
                    () -> postController.findById("1")
            );
        }

        @Test
        public void shouldFindAll() {
            Post post1 = new Post().setId(1).setTitle("Test post");
            Post post2 = new Post().setId(2).setTitle("Other test post");
            List<Post> posts = Arrays.asList(post1, post2);

            PostDto postDto1 = new PostDto();
            postDto1.setId(1);
            postDto1.setTitle("Test post");

            PostDto postDto2 = new PostDto();
            postDto2.setId(2);
            postDto2.setTitle("Other test post");

            List<PostDto> postDtos = Arrays.asList(postDto1, postDto2);

            when(postService.getAllPosts()).thenReturn(posts);
            when(postMapper.postToPostDTO(posts)).thenReturn(postDtos);

            List<PostDto> foundPostDtos = postController.findAll();

            assertThat(foundPostDtos).isEqualTo(postDtos);
        }
    }

    @Nested
    class PostControllerCreateTest {

        @Mock
        private Principal principal;

        @Test
        public void shouldCreatePost() {
            LocalDateTime now = LocalDateTime.now();

            Topic topic = new Topic().setId(1);

            CreatePostDto requestPostDto = new CreatePostDto();
            requestPostDto.setTitle("Test post");
            requestPostDto.setContent("Post content");
            requestPostDto.setTopicId(1);

            Post post = new Post();
            post.setId(1).setTitle("Test post").setCreatedAt(now).setUpdatedAt(now).setAuthor(new User().setId(1));

            PostDto responsePostDto = new PostDto();
            responsePostDto.setId(1);
            responsePostDto.setTitle("Test post");
            responsePostDto.setContent("Post content");
            responsePostDto.setCreatedAt(now);
            responsePostDto.setUpdatedAt(now);

            when(topicService.getTopicById(1)).thenReturn(Optional.of(topic));
            when(postService.createPost(any(Post.class))).thenReturn(post);
            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(new User().setId(1)));
            when(postMapper.createPostDtoToPost(requestPostDto)).thenReturn(post);
            when(postMapper.postToPostDTO(post)).thenReturn(responsePostDto);

            PostDto createdPostDto = postController.createPost(requestPostDto, principal);

            assertThat(createdPostDto.getId()).isEqualTo(1);
            assertThat(createdPostDto.getTitle()).isEqualTo("Test post");
            assertThat(createdPostDto.getContent()).isEqualTo("Post content");
            assertThat(createdPostDto.getCreatedAt()).isEqualTo(now);
            assertThat(createdPostDto.getUpdatedAt()).isEqualTo(now);
        }
    }
    /* TODO
    @Nested
    class PostControllerUpdateTest {
        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            PostDto requestPostDto = new PostDto();
            requestPostDto.setTitle("Updated test post");

            ResponseEntity<?> responseEntity = postController.update("badId1", requestPostDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldUpdatePost() {
            PostDto requestPostDto = new PostDto();
            requestPostDto.setTitle("Updated test post");

            Post post = new Post();
            post.setId(1L).setTitle("Test post").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            PostDto responsePostDto = new PostDto();
            responsePostDto.setId(post.getId());
            responsePostDto.setTitle(post.getName());
            responsePostDto.setTeacher_id(post.getTeacher().getId());
            responsePostDto.setCreatedAt(post.getCreatedAt());

            when(postService.update(1L, post)).thenReturn(post);
            when(postMapper.toEntity(requestPostDto)).thenReturn(post);
            when(postMapper.toDto(post)).thenReturn(responsePostDto);

            ResponseEntity<?> responseEntity = postController.update("1", requestPostDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(responsePostDto);
        }
    }*/

    /* TODO
    @Nested
    class PostControllerDeleteTest {
        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            ResponseEntity<?> responseEntity = postController.save("badId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnBadRequestWhenPostNotFound() {
            when(postService.getById(1L)).thenReturn(null);

            ResponseEntity<?> responseEntity = postController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldDeletePost() {
            Post post = new Post();
            post.setId(1L).setTitle("Test post").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            when(postService.getById(1L)).thenReturn(post);

            ResponseEntity<?> responseEntity = postController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }*/

}
