package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.model.Post;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = UserMapper.class)
public interface PostMapper {

    PostDto postToPostDto(Post post);

    List<PostDto> postToPostDto(List<Post> posts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Post createPostDtoToPost(CreatePostDto postDto);
}