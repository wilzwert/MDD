package com.openclassrooms.mdd.service;

import com.openclassrooms.mdd.model.Comment;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;

import java.util.List;
import java.util.Optional;


/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:34
 */

public interface PostService {
    Post createPost(Post post);
    Post updatePost(Post post);
    List<Post> getAllPosts();
    List<Post> getPostsByTopic(Topic topic);
    Optional<Post> getPostById(final int id);
    void deletePostById(final int id);
    Comment createComment(User user, int postId, Comment comment);
}