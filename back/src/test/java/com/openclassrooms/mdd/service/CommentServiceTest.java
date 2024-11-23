package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Comment;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.repository.CommentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/11/2024
 * Time:14:42
 */

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Nested
    class GetComment {
        @Test
        public void shouldGetCommentsByPost() {
           Post post = new Post().setId(1).setTitle("Post title");

           Comment comment1 = new Comment().setId(1).setPost(post);
           Comment comment2 = new Comment().setId(2).setPost(post);
           List<Comment> comments = Arrays.asList(comment1, comment2);

           when(commentRepository.findCommentsByPost(any(Post.class), any(Sort.class))).thenReturn(comments);

           List<Comment> foundComments = commentService.getCommentsByPost(post);
           assertThat(foundComments).isEqualTo(comments);

           verify(commentRepository).findCommentsByPost(any(Post.class), any(Sort.class));
        }
    }
}
