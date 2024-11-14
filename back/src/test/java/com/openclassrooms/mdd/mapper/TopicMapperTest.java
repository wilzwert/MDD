package com.openclassrooms.mdd.mapper;


import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.model.Topic;
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
public class TopicMapperTest {

    @Autowired
    private TopicMapper topicMapper;

    @Test
    public void testNullTopicToDto() {
        Topic topic = null;

        TopicDto topicDto = topicMapper.topicToTopicDto(topic);

        assertThat(topicDto).isNull();
    }

    @Test
    public void testTopicToDto() {
        LocalDateTime now = LocalDateTime.now();
        Topic topic = new Topic()
                .setId(1)
                .setTitle("test topic")
                .setDescription("this is a test topic")
                .setCreator(new User().setId(1).setUserName("testuser"))
                .setCreatedAt(now)
                .setUpdatedAt(now);
        TopicDto topicDto = topicMapper.topicToTopicDto(topic);

        assertThat(topicDto).isNotNull();
        assertThat(topicDto.getId()).isEqualTo(1);
        assertThat(topicDto.getTitle()).isEqualTo("test topic");
        assertThat(topicDto.getDescription()).isEqualTo("this is a test topic");
        assertThat(topicDto.getCreatedAt()).isEqualTo(now);
        assertThat(topicDto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    public void testNullTopicListToDto() {
        List<Topic> topics = null;

        List<TopicDto> topicDtos = topicMapper.topicToTopicDto(topics);

        assertThat(topicDtos).isNull();
    }

    @Test
    public void testTopicListToDtoList() {
        LocalDateTime now = LocalDateTime.now();

        Topic topic1 = new Topic().setId(1).setTitle("test topic1").setDescription("this is a test topic1").setCreatedAt(now).setUpdatedAt(now);
        Topic topic2 = new Topic().setId(2).setTitle("test topic2").setDescription("this is a test topic2").setCreatedAt(now).setUpdatedAt(now);

        List<TopicDto> topicDtos = topicMapper.topicToTopicDto(Arrays.asList(topic1, topic2));

        assertThat(topicDtos).isNotNull();
        assertThat(topicDtos.size()).isEqualTo(2);
        assertThat(topicDtos).extracting(TopicDto::getId).containsExactly(1, 2);
        assertThat(topicDtos).extracting(TopicDto::getTitle).containsExactly("test topic1", "test topic2");
        assertThat(topicDtos).extracting(TopicDto::getDescription).containsExactly("this is a test topic1", "this is a test topic2");
        assertThat(topicDtos).extracting(TopicDto::getCreatedAt).containsExactly(now, now);
        assertThat(topicDtos).extracting(TopicDto::getUpdatedAt).containsExactly(now, now);
    }

    @Test
    public void testNullDtoToTopic() {
        CreateTopicDto topicDto = null;

        Topic topic = topicMapper.createTopicDtoToTopic(topicDto);

        assertThat(topic).isNull();
    }

    @Test
    public void testCreateDtoToTopic() {
        CreateTopicDto topicDto = new CreateTopicDto();
        topicDto.setTitle("Test topic");
        topicDto.setDescription("Test description");
        Topic topic = topicMapper.createTopicDtoToTopic(topicDto);

        assertThat(topic).isNotNull();
        assertThat(topic.getTitle()).isEqualTo("Test topic");
        assertThat(topic.getDescription()).isEqualTo("Test description");
    }

}
