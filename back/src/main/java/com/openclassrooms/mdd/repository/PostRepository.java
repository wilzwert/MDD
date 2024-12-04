package com.openclassrooms.mdd.repository;


import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Topic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Post repository
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:58
 */

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByTopic(Topic topic);
    List<Post> findByTopicIn(List<Topic> topics, Sort sort);
}
