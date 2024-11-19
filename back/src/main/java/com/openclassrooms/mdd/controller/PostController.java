package com.openclassrooms.mdd.controller;


import com.openclassrooms.mdd.dto.request.CreateOrUpdateCommentDto;
import com.openclassrooms.mdd.dto.request.CreatePostDto;
import com.openclassrooms.mdd.dto.response.CommentDto;
import com.openclassrooms.mdd.dto.response.PostDto;
import com.openclassrooms.mdd.mapper.CommentMapper;
import com.openclassrooms.mdd.mapper.PostMapper;
import com.openclassrooms.mdd.model.*;
import com.openclassrooms.mdd.service.CommentService;
import com.openclassrooms.mdd.service.PostService;
import com.openclassrooms.mdd.service.TopicService;
import com.openclassrooms.mdd.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:32
 */
@RestController
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/posts")
@Tag(name = "Posts", description = "Post creation and update")
public class PostController {

    private final PostService postService;

    private final UserService userService;

    private final TopicService topicService;

    private final PostMapper postMapper;

    private final CommentMapper commentMapper;
    private final CommentService commentService;

    public PostController(
            PostService postService,
            UserService userService,
            TopicService topicService,
            PostMapper postMapper,
            CommentMapper commentMapper,
            CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.topicService = topicService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }

    @Operation(summary = "Retrieve all posts", description = "Retrieve all posts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema( schema = @Schema(implementation = PostDto.class)))
            })
    })
    @GetMapping()
    public List<PostDto> findAll() {
        log.info("Get all posts");
        List<Post> foundPosts = this.postService.getAllPosts();
        log.info("Got all posts {}", foundPosts);
        return postMapper.postToPostDto(foundPosts);
    }

    @Operation(summary = "Retrieve a post", description = "Retrieve a post with its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))
            })
    })
    @GetMapping("/{id}")
    public PostDto findById(@PathVariable("id") String id) {
        log.info("Get post with id {}", id);
        Optional<Post> foundPost = this.postService.getPostById(Integer.parseInt(id));

        if (foundPost.isEmpty()) {
            log.info("Post not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        return postMapper.postToPostDto(foundPost.get());
    }

    @Operation(summary = "Create a post", description = "Create a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))
            })
    })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostDto createPost(@Valid @RequestBody CreatePostDto createPostDto, Principal principal) {
        log.info("Create a post for topic {} from user {}", createPostDto.getTopicId(), principal);

        Optional<User> foundUser = userService.findUserByEmail(principal.getName());
        if(foundUser.isEmpty()) {
            log.warn("Create a post : couldn't get user info");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot get user info");
        }

        Optional<Topic> foundTopic = topicService.getTopicById(createPostDto.getTopicId());
        if(foundTopic.isEmpty()) {
            log.warn("Create a post : couldn't get topic info");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot get topic info");
        }
        Post createPost = postMapper.createPostDtoToPost(createPostDto);
        createPost.setAuthor(foundUser.get());
        createPost.setTopic(foundTopic.get());

        Post post = postService.createPost(createPost);
        log.info("Post created : {}", post);
        return postMapper.postToPostDto(post);
    }

    @Operation(summary = "Create a comment", description = "Create a comment for a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            })
    })
    @PostMapping(value = "{id}/comment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommentDto createComment(@PathVariable("id") String id, @Valid @RequestBody CreateOrUpdateCommentDto createCommentDto, Principal principal) {
        log.info("Create a comment for post {} from user {}", id, principal.getName());
        try {
            Optional<User> foundUser = userService.findUserByEmail(principal.getName());
            if (foundUser.isEmpty()) {
                log.warn("Create a comment : couldn't get user info");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot get user info");
            }

            Comment comment = postService.createComment(foundUser.get(), Integer.parseInt(id), commentMapper.commentDtoToComment(createCommentDto));
            log.info("Comment created : {}", comment.getId());
            return commentMapper.commentToCommentDto(comment);
        }
        catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    @Operation(summary = "Create a comment", description = "Create a comment for a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CommentDto.class))
            })
    })
    @GetMapping(value = "{id}/comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommentDto> getComment(@PathVariable("id") String id) {
        log.info("Get comments for post {} ", id);

        Optional<Post> foundPost = postService.getPostById(Integer.parseInt(id));
        if(foundPost.isEmpty()) {
            log.warn("Get post comments : couldn't get post info");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot get post info");
        }

        List<Comment> comments = commentService.getCommentsByPost(foundPost.get());
        return commentMapper.commentToCommentDto(comments);
    }
}
