package com.openclassrooms.mdd.repository;


import com.openclassrooms.mdd.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:58
 */

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
}
