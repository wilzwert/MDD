package com.openclassrooms.mdd.model;


/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:52
 */

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */

@Getter
@Setter
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
    private String userName;

    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Override
     * @return String the string representation of this User
     */
    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", userName=" + userName + ", createdAd=" + createdAt+", updatedAt=" + updatedAt+"]";
    }
}
