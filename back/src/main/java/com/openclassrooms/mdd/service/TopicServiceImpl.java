package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:32
 */
@Service
@Slf4j
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    public TopicServiceImpl(
            final TopicRepository topicRepository
    ) {
        this.topicRepository = topicRepository;
    }

    @Override
    public Topic createTopic(Topic topic) {
        log.info("Create Topic {}", topic.getTitle());
        Topic newTopic = topicRepository.save(topic);
        log.info("Topic {} saved", topic.getTitle());
        return newTopic;
    }

    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Override
    public Optional<Topic> getTopicById(int id) {
        return topicRepository.findById(id);
    }
}