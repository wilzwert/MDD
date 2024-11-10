package com.openclassrooms.mdd.mapper;

import com.openclassrooms.mdd.dto.request.CreateOrUpdateCommentDto;
import com.openclassrooms.mdd.dto.response.CommentDto;
import com.openclassrooms.mdd.model.Comment;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = UserMapper.class)
public interface CommentMapper {

    @Mappings({
            @Mapping(source = "author", target = "author", qualifiedByName = "userToUserDtoWithoutSubscriptions"),
            @Mapping(source = "post.id", target = "postId"),
    })
    CommentDto commentToCommentDto(Comment comment);

    Comment commentDtoToComment(CreateOrUpdateCommentDto commentDto);

    List<CommentDto> commentToCommentDto(List<Comment> comments);
}