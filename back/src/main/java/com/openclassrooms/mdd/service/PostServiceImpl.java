package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.repository.PostRepository;
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
    // TODO private final AclService aclService;

    public PostServiceImpl(
            final PostRepository postRepository
            // TODO final AclService aclService
    ) {
        this.postRepository = postRepository;
        // this.aclService = aclService;
    }

    @Override
    public Post createPost(Post post) {
        log.info("Create Post {}", post.getTitle());
        Post newPost = postRepository.save(post);
        log.info("Rental {} saved, setting owner permissions", post.getTitle());
        // TODO aclService.grantOwnerPermissions(post);
        return newPost;
    }

    @Override
    public Post updatePost(Post post) {
        return null;
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
    public Optional<Post> getPostById(int id) {
        return postRepository.findById(id);
    }
}
