package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.model.Topic;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TopicMapper {

    TopicDto topicToTopicDto(Topic topic);

    List<TopicDto> topicToTopicDto(List<Topic> topics);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    Topic createTopicDtoToTopic(CreateTopicDto topicDto);
}