package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
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
public class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

    @Test
    public void testNullPostToDto() {
        Post post = null;

        PostDto postDto = postMapper.postToPostDto(post);

        assertThat(postDto).isNull();
    }

    @Test
    public void testPostWithoutAuthorToDto() {
        LocalDateTime now = LocalDateTime.now();
        Post post = new Post()
                .setId(1)
                .setTitle("test post")
                .setContent("this is a test post")
                .setCreatedAt(now)
                .setUpdatedAt(now);

        PostDto postDto = postMapper.postToPostDto(post);

        assertThat(postDto).isNotNull();
        assertThat(postDto.getId()).isEqualTo(1);
        assertThat(postDto.getTitle()).isEqualTo("test post");
        assertThat(postDto.getContent()).isEqualTo("this is a test post");
        assertThat(postDto.getCreatedAt()).isEqualTo(now);
        assertThat(postDto.getUpdatedAt()).isEqualTo(now);
        assertThat(postDto.getAuthor()).isNull();
    }

    @Test
    public void testPostToDto() {
        LocalDateTime now = LocalDateTime.now();
        Post post = new Post()
                .setId(1)
                .setTitle("test post")
                .setContent("this is a test post")
                .setAuthor(new User().setId(1).setUserName("testuser"))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        PostDto postDto = postMapper.postToPostDto(post);

        assertThat(postDto).isNotNull();
        assertThat(postDto.getId()).isEqualTo(1);
        assertThat(postDto.getTitle()).isEqualTo("test post");
        assertThat(postDto.getContent()).isEqualTo("this is a test post");
        assertThat(postDto.getCreatedAt()).isEqualTo(now);
        assertThat(postDto.getUpdatedAt()).isEqualTo(now);
        assertThat(postDto.getAuthor().getId()).isEqualTo(1);
        assertThat(postDto.getAuthor().getUserName()).isEqualTo("testuser");

    }

    @Test
    public void testNullPostListToDto() {
        List<Post> posts = null;

        List<PostDto> postDtos = postMapper.postToPostDto(posts);

        assertThat(postDtos).isNull();
    }

    @Test
    public void testPostListToDtoList() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User().setId(1).setUserName("testuser1");
        User user2 = new User().setId(2).setUserName("testuser2");

        Post post1 = new Post().setId(1).setTitle("test post1").setContent("this is a test post1").setCreatedAt(now).setUpdatedAt(now).setAuthor(user1);
        Post post2 = new Post().setId(2).setTitle("test post2").setContent("this is a test post2").setCreatedAt(now).setUpdatedAt(now).setAuthor(user2);

        List<PostDto> postDtos = postMapper.postToPostDto(Arrays.asList(post1, post2));

        assertThat(postDtos).isNotNull();
        assertThat(postDtos.size()).isEqualTo(2);

        assertThat(postDtos).extracting(PostDto::getId).containsExactly(1, 2);
        assertThat(postDtos).extracting(PostDto::getTitle).containsExactly("test post1", "test post2");
        assertThat(postDtos).extracting(PostDto::getContent).containsExactly("this is a test post1", "this is a test post2");
        assertThat(postDtos).extracting(PostDto::getCreatedAt).containsExactly(now, now);
        assertThat(postDtos).extracting(PostDto::getUpdatedAt).containsExactly(now, now);
        assertThat(postDtos.getFirst().getAuthor().getId()).isEqualTo(1);
        assertThat(postDtos.getFirst().getAuthor().getUserName()).isEqualTo("testuser1");
        assertThat(postDtos.get(1).getAuthor().getId()).isEqualTo(2);
        assertThat(postDtos.get(1).getAuthor().getUserName()).isEqualTo("testuser2");
    }

    @Test
    public void testNullDtoToPost() {
        CreatePostDto postDto = null;

        Post post = postMapper.createPostDtoToPost(postDto);

        assertThat(post).isNull();
    }

    @Test
    public void testCreateDtoToPost() {
        CreatePostDto postDto = new CreatePostDto();
        postDto.setTitle("Test post");
        postDto.setContent("Test content");
        Post post = postMapper.createPostDtoToPost(postDto);

        assertThat(post).isNotNull();
        assertThat(post.getTitle()).isEqualTo("Test post");
        assertThat(post.getContent()).isEqualTo("Test content");
    }
}
