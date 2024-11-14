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
    // TODO private final AclService aclService;

    public TopicServiceImpl(
            final TopicRepository topicRepository
            // TODO final AclService aclService
    ) {
        this.topicRepository = topicRepository;
        // this.aclService = aclService;
    }

    @Override
    public Topic createTopic(Topic topic) {
        log.info("Create Topic {}", topic.getTitle());
        Topic newTopic = topicRepository.save(topic);
        log.info("Rental {} saved, setting owner permissions", topic.getTitle());
        // TODO aclService.grantOwnerPermissions(topic);
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
