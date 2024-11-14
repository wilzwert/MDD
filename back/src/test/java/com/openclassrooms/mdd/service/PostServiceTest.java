package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Comment;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import com.openclassrooms.mdd.repository.CommentRepository;
import com.openclassrooms.mdd.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/11/2024
 * Time:14:42
 */

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Nested
    class CreatePostTest {
        @Test
        public void shouldCreatePost() {
            Post post = new Post();

            when(postRepository.save(post)).thenReturn(post);

            Post createdPost = postService.createPost(post);

            verify(postRepository).save(post);
            assertThat(createdPost).isNotNull().isEqualTo(post);
        }
    }

    @Nested
    class GetPost {
        @Test
        public void shouldGetAllPosts() {
            Post post1 = new Post();
            Post post2 = new Post();
            List<Post> posts = Arrays.asList(post1, post2);

            when(postRepository.findAll()).thenReturn(posts);

            List<Post> allPosts = postService.getAllPosts();
            verify(postRepository).findAll();
            assertThat(allPosts).isEqualTo(posts);
        }

        @Test
        public void shouldFindAPostByItsId() {
            Post post = new Post().setId(1);

            when(postRepository.findById(1)).thenReturn(Optional.of(post));

            Optional<Post> postOptional = postService.getPostById(1);

            verify(postRepository).findById(1);
            assertThat(postOptional).isPresent();
            assertThat(postOptional.get()).isEqualTo(post);
        }

        @Test
        public void shouldReturnEmptyOptionalWhePostNotFound() {
            when(postRepository.findById(1)).thenReturn(Optional.empty());

            Optional<Post> postOptional = postService.getPostById(1);

            verify(postRepository).findById(1);
            assertThat(postOptional).isEmpty();
        }

        @Test
        public void shouldGetPostsByTopic() {
            Topic topic = new Topic().setId(1);
            Post post1 = new Post();
            Post post2 = new Post();
            List<Post> posts = Arrays.asList(post1, post2);

            when(postRepository.findByTopic(topic)).thenReturn(posts);

            List<Post> allPosts = postService.getPostsByTopic(topic);
            verify(postRepository).findByTopic(topic);
            assertThat(allPosts).isEqualTo(posts);
        }
    }

    @Nested
    class CreateCommentTest {

        @Test
        public void shouldThrowEntityNotFoundExceptionWhenPostNotFound() {
            User user = new User().setId(1);
            when(postRepository.findById(1)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> postService.createComment(user, 1, new Comment()));
        }

        @Test
        public void shouldCreateComment() {
            User user = new User().setId(1);
            Post post = new Post().setId(1);
            Comment comment = new Comment().setId(1);
            when(postRepository.findById(1)).thenReturn(Optional.of(post));
            when(commentRepository.save(comment)).thenReturn(comment);

            Comment createdComment = postService.createComment(user, 1, comment);
            verify(postRepository).findById(1);
            verify(commentRepository).save(comment);
            assertThat(createdComment).isNotNull().isEqualTo(comment);
        }
    }
}
