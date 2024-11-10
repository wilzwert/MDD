package com.openclassrooms.mdd.repository;

import com.openclassrooms.mdd.model.Comment;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:15:58
 */

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    void findCommentsByPost(Post post);
    void findCommentsByAuthor(User user);
}
