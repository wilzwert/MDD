package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.repository.TopicRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/11/2024
 * Time:14:28
 */

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {

    @InjectMocks
    private TopicServiceImpl topicService;

    @Mock
    private TopicRepository topicRepository;

    @Nested
    class CreateTopicTest {
        @Test
        public void shouldCreateTopic() {
            Topic topic = new Topic();

            when(topicRepository.save(topic)).thenReturn(topic);

            Topic createdTopic = topicService.createTopic(topic);

            verify(topicRepository).save(topic);
            assertThat(createdTopic).isNotNull().isEqualTo(topic);
        }
    }

    @Nested
    class GetTopic {
        @Test
        public void shouldGetAllTopics() {
            Topic topic1 = new Topic();
            Topic topic2 = new Topic();
            List<Topic> topics = Arrays.asList(topic1, topic2);

            when(topicRepository.findAll()).thenReturn(topics);

            List<Topic> allTopics = topicService.getAllTopics();
            verify(topicRepository).findAll();
            assertThat(allTopics).isEqualTo(topics);
        }

        @Test
        public void shouldFindATopicByItsId() {
            Topic topic = new Topic().setId(1);

            when(topicRepository.findById(1)).thenReturn(Optional.of(topic));

            Optional<Topic> topicOptional = topicService.getTopicById(1);

            verify(topicRepository).findById(1);
            assertThat(topicOptional.isPresent()).isTrue();
            assertThat(topicOptional.get()).isEqualTo(topic);
        }

        @Test
        public void shouldReturnEmptyOptionalWheTopicNotFound() {
            when(topicRepository.findById(1)).thenReturn(Optional.empty());

            Optional<Topic> topicOptional = topicService.getTopicById(1);

            verify(topicRepository).findById(1);
            assertThat(topicOptional.isEmpty()).isTrue();
        }
    }
}
