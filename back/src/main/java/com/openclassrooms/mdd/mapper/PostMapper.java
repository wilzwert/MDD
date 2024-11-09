package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = UserMapper.class)
public interface PostMapper {

    PostDto postToPostDTO(Post post);

    List<PostDto> postToPostDTO(List<Post> posts);

    Post createPostDtoToPost(CreatePostDto postDto);
}