package com.openclassrooms.mdd.controller;


import com.openclassrooms.mdd.dto.request.CreateTopicDto;
import com.openclassrooms.mdd.dto.response.TopicDto;
import com.openclassrooms.mdd.mapper.TopicMapper;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
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
@RequestMapping("/api/topic")
@Tag(name = "Topics", description = "Topic creation and update")
public class TopicController {

    private final TopicService topicService;

    private final UserService userService;

    private final TopicMapper topicMapper;

    public TopicController(TopicService topicService, UserService userService, TopicMapper topicMapper) {
        this.topicService = topicService;
        this.userService = userService;
        this.topicMapper = topicMapper;
    }

    @Operation(summary = "Retrieve all topics", description = "Retrieve all topics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema( schema = @Schema(implementation = TopicDto.class)))
            })
    })
    @GetMapping()
    public List<TopicDto> findAll() {
        log.info("Get all topics");
        List<Topic> foundTopics = this.topicService.getAllTopics();
        log.info("Got all topics {}", foundTopics);
        return topicMapper.topicToTopicDTO(foundTopics);
    }

    @Operation(summary = "Retrieve a topic", description = "Retrieve a topic with its id")
    @GetMapping("/{id}")
    public TopicDto findById(@PathVariable("id") String id) {
        log.info("Get topic with id {}", id);
        Optional<Topic> foundTopic = this.topicService.getTopicById(Integer.parseInt(id));

        if (foundTopic.isEmpty()) {
            log.info("Topic not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        }

        return topicMapper.topicToTopicDTO(foundTopic.get());
    }

    @Operation(summary = "Create a topic", description = "Create a topic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TopicDto.class))
            })
    })
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TopicDto createTopic(@Valid @ModelAttribute CreateTopicDto createTopicDto, Principal principal) {
        log.info("Create a topic {}", principal);
        try {
            Optional<User> foundUser = userService.findUserByEmail(principal.getName());
            if(foundUser.isEmpty()) {
                log.warn("Create a topic : couldn't get user info");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot get user info");
            }
            Topic createTopic = topicMapper.createTopicDtoToTopic(createTopicDto);
            createTopic.setCreator(foundUser.get());
            Topic topic = topicService.createTopic(createTopic);
            log.info("Topic created : {}", topic);
            return topicMapper.topicToTopicDTO(topic);
        }
        catch(Exception e) {
            log.error("Create a topic: topic could not be created", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Topic could not be created");
        }
    }
}
