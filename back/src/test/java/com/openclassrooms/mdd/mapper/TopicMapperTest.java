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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

        TopicDto topicDto = topicMapper.topicToTopicDTO(topic);

        assertThat(topicDto).isNull();
    }

    @Test
    public void testTopicWithoutUsersToDto() {
        Topic topic = new Topic().setId(1);

        TopicDto topicDto = topicMapper.topicToTopicDTO(topic);

        assertThat(topicDto).isNotNull();
        assertThat(topicDto.getId()).isEqualTo(1);
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
        TopicDto topicDto = topicMapper.topicToTopicDTO(topic);

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

        List<TopicDto> topicDtos = topicMapper.topicToTopicDTO(topics);

        assertThat(topicDtos).isNull();
    }

    @Test
    public void testTopicListToDtoList() {
        List<Topic> topics = new ArrayList<>();
        List<List<Long>> expectedUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Topic topic = new Topic();
            topic.setId(i);
            topic.setCreator(new User().setId(i).setUserName("testuser"+i));
            topic.setTitle("Test topic "+i);
            topic.setCreatedAt(LocalDateTime.now());
            topic.setUpdatedAt(LocalDateTime.now());
            topics.add(topic);
        }

        List<TopicDto> topicDtos = topicMapper.topicToTopicDTO(topics);

        assertThat(topicDtos).isNotNull();
        assertThat(topicDtos).extracting(TopicDto::getId).containsExactlyElementsOf(topics.stream().map(Topic::getId).collect(Collectors.toList()));
        assertThat(topicDtos).extracting(TopicDto::getCreatedAt).containsExactlyElementsOf(topics.stream().map(Topic::getCreatedAt).collect(Collectors.toList()));
        assertThat(topicDtos).extracting(TopicDto::getUpdatedAt).containsExactlyElementsOf(topics.stream().map(Topic::getUpdatedAt).collect(Collectors.toList()));
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
