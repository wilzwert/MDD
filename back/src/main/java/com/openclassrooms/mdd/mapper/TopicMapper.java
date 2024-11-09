package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.model.Topic;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TopicMapper {

    TopicDto topicToTopicDTO(Topic topic);

    List<TopicDto> topicToTopicDTO(List<Topic> topics);

    Topic createTopicDtoToTopic(CreateTopicDto topicDto);
}