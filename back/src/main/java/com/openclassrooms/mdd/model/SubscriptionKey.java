package com.openclassrooms.mdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class SubscriptionKey implements Serializable {
    @Column(name = "user_id")
    int userId;

    @Column(name = "topic_id")
    int topicId;
}
