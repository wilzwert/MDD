package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreateOrUpdateCommentDto;
import com.openclassrooms.mdd.dto.response.CommentDto;
import com.openclassrooms.mdd.model.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:39
 */

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Tag("Mapper")
public class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void testNullCommentToDto() {
        Comment comment = null;

        CommentDto commentDto = commentMapper.commentToCommentDto(comment);

        assertThat(commentDto).isNull();
    }

    @Test
    public void testCommentWithoutAuthorToDto() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment()
                .setId(1)
                .setContent("this is a test comment")
                .setPost(new Post().setId(1).setTitle("this is a test post"))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        CommentDto commentDto = commentMapper.commentToCommentDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1);
        assertThat(commentDto.getContent()).isEqualTo("this is a test comment");
        assertThat(commentDto.getPostId()).isEqualTo(1);
        assertThat(commentDto.getCreatedAt()).isEqualTo(now);
        assertThat(commentDto.getUpdatedAt()).isEqualTo(now);
        assertThat(commentDto.getAuthor()).isNull();
    }

    @Test
    public void testCommentWithoutPostToDto() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment()
                .setId(1)
                .setContent("this is a test comment")
                .setAuthor(new User().setId(1))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        CommentDto commentDto = commentMapper.commentToCommentDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1);
        assertThat(commentDto.getContent()).isEqualTo("this is a test comment");
        assertThat(commentDto.getPostId()).isEqualTo(0);
        assertThat(commentDto.getCreatedAt()).isEqualTo(now);
        assertThat(commentDto.getUpdatedAt()).isEqualTo(now);
        assertThat(commentDto.getAuthor().getId()).isEqualTo(1);
        assertThat(commentDto.getAuthor().getSubscriptions()).isNull();
    }


    @Test
    public void testCommentToDto() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User().setId(1).setUserName("testuser");
        Topic topic = new Topic().setId(1).setTitle("testtopic");
        Subscription subscription = new Subscription().setUser(user).setTopic(topic);
        user.setSubscriptions(Collections.singletonList(subscription));
        Comment comment = new Comment()
                .setId(1)
                .setContent("this is a test comment")
                .setAuthor(user)
                .setPost(new Post().setId(1).setTitle("this is a test post"))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        CommentDto commentDto = commentMapper.commentToCommentDto(comment);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1);
        assertThat(commentDto.getPostId()).isEqualTo(1);
        assertThat(commentDto.getContent()).isEqualTo("this is a test comment");
        assertThat(commentDto.getCreatedAt()).isEqualTo(now);
        assertThat(commentDto.getUpdatedAt()).isEqualTo(now);
        assertThat(commentDto.getAuthor().getId()).isEqualTo(1);
        assertThat(commentDto.getAuthor().getUserName()).isEqualTo("testuser");
        assertThat(commentDto.getAuthor().getSubscriptions()).isNull();

    }

    @Test
    public void testNullCommentListToDto() {
        List<Comment> comments = null;

        List<CommentDto> commentDtos = commentMapper.commentToCommentDto(comments);

        assertThat(commentDtos).isNull();
    }

    @Test
    public void testCommentListToDtoList() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User().setId(1).setUserName("testuser1");
        User user2 = new User().setId(2).setUserName("testuser2");

        Comment comment1 = new Comment().setId(1).setContent("this is a test comment1").setCreatedAt(now).setUpdatedAt(now).setAuthor(user1);
        Comment comment2 = new Comment().setId(2).setContent("this is a test comment2").setCreatedAt(now).setUpdatedAt(now).setAuthor(user2);

        List<CommentDto> commentDtos = commentMapper.commentToCommentDto(Arrays.asList(comment1, comment2));

        assertThat(commentDtos).isNotNull();
        assertThat(commentDtos.size()).isEqualTo(2);

        assertThat(commentDtos).extracting(CommentDto::getId).containsExactly(1, 2);
        assertThat(commentDtos).extracting(CommentDto::getContent).containsExactly("this is a test comment1", "this is a test comment2");
        assertThat(commentDtos).extracting(CommentDto::getCreatedAt).containsExactly(now, now);
        assertThat(commentDtos).extracting(CommentDto::getUpdatedAt).containsExactly(now, now);
        assertThat(commentDtos.getFirst().getAuthor().getId()).isEqualTo(1);
        assertThat(commentDtos.getFirst().getAuthor().getUserName()).isEqualTo("testuser1");
        // check that userToUserDtoWithoutSubscriptions is used to map User
        assertThat(commentDtos.getFirst().getAuthor().getSubscriptions()).isNull();
        assertThat(commentDtos.get(1).getAuthor().getId()).isEqualTo(2);
        assertThat(commentDtos.get(1).getAuthor().getUserName()).isEqualTo("testuser2");
        // check that userToUserDtoWithoutSubscriptions is used to map User
        assertThat(commentDtos.get(1).getAuthor().getSubscriptions()).isNull();

    }

    @Test
    public void testNullDtoToComment() {
        CreateOrUpdateCommentDto commentDto = null;

        Comment comment = commentMapper.commentDtoToComment(commentDto);

        assertThat(comment).isNull();
    }

    @Test
    public void testCreateDtoToComment() {
        CreateOrUpdateCommentDto commentDto = new CreateOrUpdateCommentDto();
        commentDto.setContent("Test content");
        Comment comment = commentMapper.commentDtoToComment(commentDto);

        assertThat(comment).isNotNull();
        assertThat(comment.getContent()).isEqualTo("Test content");
    }
}
