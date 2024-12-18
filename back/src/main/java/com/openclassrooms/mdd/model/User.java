package com.openclassrooms.mdd.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a use, may be persisted
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:52
 */

@Data
@Accessors(chain = true)
@Entity
@Table(name="user")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "user_name", unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private List<Topic> topics;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Post> posts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Subscription> subscriptions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @Nullable
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private RefreshToken refreshToken;

    /**
     * Override
     * @return String the string representation of this User
     */
    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", username=" + username + ", createdAd=" + createdAt+", updatedAt=" + updatedAt+"]";
    }
}
