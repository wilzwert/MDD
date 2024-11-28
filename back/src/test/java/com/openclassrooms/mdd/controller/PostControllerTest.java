package com.openclassrooms.mdd.controller;

import com.openclassrooms.mdd.dto.request.CreateOrUpdateCommentDto;
import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.CommentDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.mapper.CommentMapper;
import com.openclassrooms.mdd.mapper.PostMapper;
import com.openclassrooms.mdd.model.*;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.service.CommentService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    private CommentService commentService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private Principal principal;

    @Nested
    class PostControllerRetrievalTest {
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
            when(postMapper.postToPostDto(post)).thenReturn(postDto);

            PostDto responsePostDto = postController.findById("1");

            assertThat(responsePostDto).isEqualTo(postDto);
        }

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            assertThrows(NumberFormatException.class, () -> postController.findById("badId1"));
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
        public void shouldReturnUnAuthorizedWhenUserNotFound() {
            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> postController.findAll(principal));
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldFindAllForUser() {
            User user = new User().setId(1).setUsername("testuser").setEmail("test@example.com");

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

            when(principal.getName()).thenReturn("test@example.com");
            when(userService.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(postService.getPostsByUserSubscriptions(user)).thenReturn(posts);
            when(postMapper.postToPostDto(posts)).thenReturn(postDtos);

            List<PostDto> foundPostDtos = postController.findAll(principal);

            verify(principal).getName();
            verify(userService).findUserByEmail("test@example.com");
            verify(postService).getPostsByUserSubscriptions(user);
            verify(postMapper).postToPostDto(posts);
            assertThat(foundPostDtos).isEqualTo(postDtos);
        }
    }

    @Nested
    class PostControllerCreateTest {

        @Mock
        private Principal principal;

        @Test
        public void shouldThrowUnauthorizedResponseStatusExceptionWhenUserNotFound() {
            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTitle("Test post");
            createPostDto.setContent("Post content");
            createPostDto.setTopicId(1);

            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.empty());

            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> postController.createPost(createPostDto, principal));

            verify(principal).getName();
            verify(userService).findUserByEmail("user@example.com");

            assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldThrowBadRequestResponseStatusExceptionWhenTopicNotFound() {
            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTitle("Test post");
            createPostDto.setContent("Post content");
            createPostDto.setTopicId(1);

            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(new User().setId(1)));
            when(topicService.getTopicById(1)).thenReturn(Optional.empty());

            ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> postController.createPost(createPostDto, principal));

            verify(principal).getName();
            verify(userService).findUserByEmail("user@example.com");
            verify(topicService).getTopicById(1);


            assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldCreatePost() {
            LocalDateTime now = LocalDateTime.now();

            Topic topic = new Topic().setId(1);

            CreatePostDto createPostDto = new CreatePostDto();
            createPostDto.setTitle("Test post");
            createPostDto.setContent("Post content");
            createPostDto.setTopicId(1);

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
            when(postMapper.createPostDtoToPost(createPostDto)).thenReturn(post);
            when(postMapper.postToPostDto(post)).thenReturn(responsePostDto);

            PostDto createdPostDto = postController.createPost(createPostDto, principal);

            verify(topicService).getTopicById(1);
            verify(postService).createPost(any(Post.class));
            verify(principal).getName();
            verify(userService).findUserByEmail("user@example.com");
            verify(postMapper).createPostDtoToPost(createPostDto);
            verify(postMapper).postToPostDto(post);

            assertThat(createdPostDto.getId()).isEqualTo(1);
            assertThat(createdPostDto.getTitle()).isEqualTo("Test post");
            assertThat(createdPostDto.getContent()).isEqualTo("Post content");
            assertThat(createdPostDto.getCreatedAt()).isEqualTo(now);
            assertThat(createdPostDto.getUpdatedAt()).isEqualTo(now);
        }
    }

    @Nested
    class PostControllerCommentRetrievalTest {

        @Test
        public void shouldThrowNotFoundResponseStatusExceptionWhenPostNotFound() {
            when(postService.getPostById(1)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> postController.getComment("1"));
            verify(postService).getPostById(1);
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);


        }
        @Test
        public void shouldReturnPostComments() {
            LocalDateTime now = LocalDateTime.now();

            Post post = new Post().setId(1).setTitle("Test post").setCreatedAt(LocalDateTime.now()).setUpdatedAt(LocalDateTime.now());
            User user = new User().setId(1);
            Comment comment1 = new Comment().setId(1).setContent("Comment content").setAuthor(user).setPost(post);
            Comment comment2 = new Comment().setId(2).setContent("Comment content 2").setAuthor(user).setPost(post);
            List<Comment> comments = Arrays.asList(comment1, comment2);

            CommentDto commentDto1 = new CommentDto();
            commentDto1.setId(1);
            commentDto1.setContent("Comment content");
            commentDto1.setCreatedAt(now);
            commentDto1.setUpdatedAt(now);

            CommentDto commentDto2 = new CommentDto();
            commentDto2.setId(2);
            commentDto2.setContent("Comment content 2");
            commentDto2.setCreatedAt(now);
            commentDto2.setUpdatedAt(now);

            when(postService.getPostById(1)).thenReturn(Optional.of(post));
            when(commentService.getCommentsByPost(post)).thenReturn(comments);
            when(commentMapper.commentToCommentDto(comments)).thenReturn(Arrays.asList(commentDto1, commentDto2));

            List<CommentDto> responseCommentDtos = postController.getComment("1");

            verify(postService).getPostById(1);
            verify(commentService).getCommentsByPost(post);
            verify(commentMapper).commentToCommentDto(comments);

            assertThat(responseCommentDtos).hasSize(2);
            assertThat(responseCommentDtos).extracting(CommentDto::getId).containsExactly(1, 2);
            assertThat(responseCommentDtos).extracting(CommentDto::getContent).containsExactly("Comment content", "Comment content 2");
            assertThat(responseCommentDtos).extracting(CommentDto::getCreatedAt).containsExactly(now, now);
            assertThat(responseCommentDtos).extracting(CommentDto::getUpdatedAt).containsExactly(now, now);
        }
    }

    @Nested
    class PostControllerCreateCommentTest {

        @Mock
        private Principal principal;

        @Test
        public void shouldThrowUnauthorizedWhenUserNotFound() {
            CreateOrUpdateCommentDto requestCommentDto = new CreateOrUpdateCommentDto();
            requestCommentDto.setContent("Comment content");

            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> postController.createComment("1", requestCommentDto, principal));

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldThrowNotFoundWhenPostNotFound() {
            CreateOrUpdateCommentDto requestCommentDto = new CreateOrUpdateCommentDto();
            requestCommentDto.setContent("Comment content");

            User user = new User().setId(1);
            Comment comment = new Comment().setContent("Comment content");

            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(commentMapper.commentDtoToComment(requestCommentDto)).thenReturn(comment);
            when(postService.createComment(user, 1, comment)).thenThrow(new EntityNotFoundException("post not found"));

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> postController.createComment("1", requestCommentDto, principal));

            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldCreateComment() {
            LocalDateTime now = LocalDateTime.now();

            User user = new User().setId(1);

            CreateOrUpdateCommentDto requestCommentDto = new CreateOrUpdateCommentDto();
            requestCommentDto.setContent("Comment content");

            Comment comment = new Comment().setId(1).setContent("Comment content");


            CommentDto responseCommentDto = new CommentDto();
            responseCommentDto.setId(1);
            responseCommentDto.setCreatedAt(now);
            responseCommentDto.setUpdatedAt(now);
            responseCommentDto.setContent("Comment content");

            when(principal.getName()).thenReturn("user@example.com");
            when(userService.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
            when(postService.createComment(eq(user), eq(1), any(Comment.class))).thenAnswer(i -> i.getArgument(2));
            when(commentMapper.commentDtoToComment(requestCommentDto)).thenReturn(comment);
            when(commentMapper.commentToCommentDto(any(Comment.class))).thenReturn(responseCommentDto);

            CommentDto resultCommentDto = postController.createComment("1", requestCommentDto, principal);

            verify(userService).findUserByEmail("user@example.com");
            verify(postService).createComment(eq(user), eq(1), any(Comment.class));
            verify(commentMapper).commentDtoToComment(requestCommentDto);
            verify(commentMapper).commentToCommentDto(any(Comment.class));

            assertThat(resultCommentDto.getId()).isEqualTo(1);
            assertThat(resultCommentDto.getCreatedAt()).isEqualTo(now);
            assertThat(resultCommentDto.getUpdatedAt()).isEqualTo(now);
            assertThat(resultCommentDto.getContent()).isEqualTo("Comment content");

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
