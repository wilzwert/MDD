package com.openclassrooms.mdd.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:23
 */

@Data
@Accessors(chain = true)
@Entity
@Table(name="topic")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String description;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = true)
    // let db handle cascade deletion
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User creator;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "topic")
    private List<Post> posts;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<Subscription> subscriptions;

    /**
     * Override
     * @return String the string representation of this Topic
     */
    @Override
    public String toString() {
        return "Topic [id=" + id + ", title=" + title +"]";
    }
}
