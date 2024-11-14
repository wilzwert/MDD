package com.openclassrooms.mdd.service;

import com.openclassrooms.mdd.model.Topic;

import java.util.List;
import java.util.Optional;


/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:34
 */

public interface TopicService {
    Topic createTopic(Topic topic);
    List<Topic> getAllTopics();
    Optional<Topic> getTopicById(final int id);
}