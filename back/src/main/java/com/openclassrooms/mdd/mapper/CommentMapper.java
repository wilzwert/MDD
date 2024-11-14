package com.openclassrooms.mdd.mapper;

import com.openclassrooms.mdd.dto.request.CreateOrUpdateCommentDto;
import com.openclassrooms.mdd.dto.response.CommentDto;
import com.openclassrooms.mdd.model.Comment;
import org.mapstruct.*;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = UserMapper.class)
public interface CommentMapper {


    @Mapping(source = "author", target = "author", qualifiedByName = "userToUserDtoWithoutSubscriptions")
    @Mapping(source = "post.id", target = "postId")
    CommentDto commentToCommentDto(Comment comment);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "post", ignore = true)
    Comment commentDtoToComment(CreateOrUpdateCommentDto commentDto);

    List<CommentDto> commentToCommentDto(List<Comment> comments);
}