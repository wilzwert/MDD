package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.*;
import com.openclassrooms.mdd.repository.CommentRepository;
import com.openclassrooms.mdd.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    // TODO private final AclService aclService;

    public PostServiceImpl(
            final PostRepository postRepository,
            final CommentRepository commentRepository
            // TODO final AclService aclService
        ) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        // this.aclService = aclService;
    }

    @Override
    public Post createPost(Post post) {
        log.info("Create Post {}", post.getTitle());
        Post newPost = postRepository.save(post);
        log.info("Post {} saved", post.getTitle());
        // TODO aclService.grantOwnerPermissions(post);
        return newPost;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostsByTopic(Topic topic) {
        return postRepository.findByTopic(topic);
    }

    @Override
    public List<Post> getPostsByUserSubscriptions(User user) {
        return postRepository.findByTopicIn(user.getSubscriptions().stream().map(Subscription::getTopic).toList());
    }

    @Override
    public Optional<Post> getPostById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public Comment createComment(User user, int postId, Comment comment) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("post not found"));
        comment.setPost(post);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }
}
